package net.cyanmarine.simple_veinminer.mixin;

import me.shedaniel.math.Color;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.cyanmarine.simple_veinminer.client.SimpleVeinminerClient;
import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.cyanmarine.simple_veinminer.config.SimpleConfigClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin (WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow @Nullable private ClientWorld world;

    @Shadow protected static void drawShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {}

    BlockPos currentlyOutliningPos;
    BlockState currentlyOutliningState;
    ArrayList<BlockPos> blocksToOutline;
    float r, g, b, a;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true)
    public void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (entity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) entity;
            SimpleConfigClient.Outline outline = SimpleVeinminerClient.getConfig().outline;
            SimpleConfig.SimpleConfigCopy worldConfig = SimpleVeinminerClient.getWorldConfig();
            if (SimpleVeinminerClient.veinMineKeybind.isPressed() && SimpleVeinminer.canVeinmine(player, world, pos, state, worldConfig.restrictions) && outline.outlineBlocks) {
                ci.cancel();
                if (!pos.equals(currentlyOutliningPos) || !state.equals(currentlyOutliningState)) {
                    Color outlineColor = outline.outlineColor;

                    blocksToOutline = getBlocksToOutline(pos, state);
                    currentlyOutliningPos = pos;
                    currentlyOutliningState = state;

                    r = outlineColor.getRed() / 255.0f;
                    g = outlineColor.getGreen() / 255.0f;
                    b = outlineColor.getBlue() / 255.0f;
                    a = 100.0f / 255.0f;
                }

                for (int i = 0; i < blocksToOutline.size(); i++) {
                    BlockPos currentPos = blocksToOutline.get(i);
                    outline(matrices, vertexConsumer, entity, d, e, f, currentPos, world.getBlockState(currentPos), r, g, b, a);
                }

            } else if (currentlyOutliningPos != null) {
                currentlyOutliningPos = null;
                currentlyOutliningState = null;
                blocksToOutline = null;
            }
        }
    }

    private ArrayList<BlockPos> getBlocksToOutline(BlockPos pos, BlockState state) {
        SimpleConfig.SimpleConfigCopy worldConfig = SimpleVeinminerClient.getWorldConfig();

        ArrayList<BlockPos> willVeinmine = SimpleVeinminer.getBlocksToVeinmine(world, pos, state, worldConfig.maxBlocks);
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
        drawShapeOutline(matrices, vertexConsumer, state.getOutlineShape(world, pos, ShapeContext.of(entity)), (double) pos.getX() - d, (double) pos.getY() - e, (double) pos.getZ() - f, r, g, b, a);
    }
}