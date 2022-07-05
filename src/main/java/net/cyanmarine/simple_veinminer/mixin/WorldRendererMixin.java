package net.cyanmarine.simple_veinminer.mixin;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.shedaniel.math.Color;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.cyanmarine.simple_veinminer.client.SimpleVeinminerClient;
import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.cyanmarine.simple_veinminer.config.SimpleConfigClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;

import static net.cyanmarine.simple_veinminer.SimpleVeinminer.SERVER_SIDE_VEINMINING;
import static net.cyanmarine.simple_veinminer.SimpleVeinminer.getBlockHitResult;

@Mixin (WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow @Nullable private ClientWorld world;

    @Shadow
    protected static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {}

    @Shadow @Final private Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos;
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions;

    @Shadow public abstract void setBlockBreakingInfo(int entityId, BlockPos pos, int stage);

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private static Logger LOGGER;

    @Shadow protected abstract void removeBlockBreakingInfo(BlockBreakingInfo info);

    Item holding;
    BlockPos currentlyOutliningPos;
    BlockState currentlyOutliningState;
    ArrayList<BlockPos> blocksToOutline;
    ArrayList<BlockPos> beingBroken;
    float r, g, b, a;
    BlockHitResult blockHitResult;

    int delay = 0;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true)
    public void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (entity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) entity;
            SimpleConfigClient.Outline outline = SimpleVeinminerClient.getConfig().outline;
            SimpleConfig.SimpleConfigCopy worldConfig = SimpleVeinminerClient.getWorldConfig();
            if ((SimpleVeinminerClient.veinMineKeybind.isPressed() || (SimpleVeinminerClient.isVeinMiningServerSide && player.isSneaking())) && SimpleVeinminer.canVeinmine(player, world, pos, state, worldConfig.restrictions) && outline.outlineBlocks) {
                ci.cancel();

                Item hand = client.player.getMainHandStack().getItem();

                if (delay % 10 == 0 || !pos.equals(currentlyOutliningPos) || !state.equals(currentlyOutliningState) || !hand.equals(holding)) {
                    holding = hand;
                    Color outlineColor = outline.outlineColor;

                    blocksToOutline = getBlocksToOutline(pos, state, player);
                    currentlyOutliningPos = pos;
                    currentlyOutliningState = state;

                    r = outlineColor.getRed() / 255.0f;
                    g = outlineColor.getGreen() / 255.0f;
                    b = outlineColor.getBlue() / 255.0f;
                    a = 100.0f / 255.0f;

                    delay = 0;
                }

                for (int i = 0; i < blocksToOutline.size(); i++) {
                    BlockPos currentPos = blocksToOutline.get(i);
                    outline(matrices, vertexConsumer, entity, d, e, f, currentPos, world.getBlockState(currentPos), r, g, b, a);
                }

                delay++;
            } else if (currentlyOutliningPos != null) {
                clearBlockBreakingProgressions();
                currentlyOutliningPos = null;
                currentlyOutliningState = null;
                blocksToOutline = null;
                holding = null;
                delay = 0;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V")
    public void renderInject(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        clearBlockBreakingProgressions();
        if (SimpleVeinminerClient.getConfig().showMiningProgress && blocksToOutline != null && currentlyOutliningPos != null) {
            SortedSet<BlockBreakingInfo> blockBreakingSet = blockBreakingProgressions.get(currentlyOutliningPos.asLong());
            if (blockBreakingSet != null) {
                BlockBreakingInfo blockBreakingProgress = blockBreakingSet.last();
                int stage = blockBreakingProgress.getStage();

                if (beingBroken == null)
                    beingBroken = (ArrayList<BlockPos>) blocksToOutline.clone();

                for (int i = 0; i < blocksToOutline.size(); i++) {
                    BlockPos currentPos = blocksToOutline.get(i);
                    if (currentPos.equals(currentlyOutliningPos)) continue;
                    BlockBreakingInfo newBlockBreakingProgress = new BlockBreakingInfo(blockBreakingProgress.hashCode(), currentPos);
                    newBlockBreakingProgress.setStage(stage);
                    (blockBreakingProgressions.computeIfAbsent(currentPos.asLong(), (l) -> Sets.newTreeSet())).add(newBlockBreakingProgress);
                }
            }
        }
    }


    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/render/WorldRenderer;removeBlockBreakingInfo(Lnet/minecraft/client/render/BlockBreakingInfo;)V", cancellable = true)
    public void removeBlockBreakingInfoReplace(BlockBreakingInfo info, CallbackInfo ci) {
        ci.cancel();
        long l = info.getPos().asLong();
        Set<BlockBreakingInfo> set = (Set)this.blockBreakingProgressions.get(l);
        if (set != null) {
            set.remove(info);
            if (set.isEmpty()) {
                this.blockBreakingProgressions.remove(l);
            }
        }
    }

    private void clearBlockBreakingProgressions() {
        if (beingBroken != null && blockBreakingProgressions != null)
            for (int i = 0; i < beingBroken.size(); i++) {
                BlockPos pos = beingBroken.get(i);
                if (pos.equals(currentlyOutliningPos)) continue;
                long l = pos.asLong();
                SortedSet<BlockBreakingInfo> set = this.blockBreakingProgressions.get(l);
                if (set != null) {
                    BlockBreakingInfo info = set.last();
                    removeBlockBreakingInfo(info);
                }
            }
        beingBroken = null;
    }

    private ArrayList<BlockPos> getBlocksToOutline(BlockPos pos, BlockState state, PlayerEntity player) {
        ArrayList<BlockPos> willVeinmine = SimpleVeinminer.getBlocksToVeinmine(pos, state, SimpleVeinminer.getMaxBlocks(holding), player);
        ArrayList<BlockPos> willOutline = new ArrayList<>();

        for (int i = 0; i < willVeinmine.size(); i++) {
            BlockPos currentPos = willVeinmine.get(i);
            if(hasFaceVisible(currentPos)) willOutline.add(currentPos);
        }
        // SimpleVeinminerClient.LOGGER.info(willVeinmine.size() + " " + willOutline.size());
        return willOutline;
    }

    private boolean hasFaceVisible(BlockPos pos) {
        int[] coords = {0, 0, 0};
        int i, j;

        for (i = 0; i < 3; i++)
            for (j = -1; j <= 1; j += 2) {
                coords[i] = j;
                if (!world.getBlockState(pos.add(coords[0], coords[1], coords[2])).isSolidBlock(world, pos)) return true;
                coords[i] = 0;
            }

        return false;
    }

    private void outline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos pos, BlockState state, float r, float g, float b, float a) {
        drawCuboidShapeOutline(matrices, vertexConsumer, state.getOutlineShape(world, pos, ShapeContext.of(entity)), (double) pos.getX() - d, (double) pos.getY() - e, (double) pos.getZ() - f, r, g, b, a);
    }
}

