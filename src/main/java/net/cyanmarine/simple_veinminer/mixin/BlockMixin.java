package net.cyanmarine.simple_veinminer.mixin;

import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At("HEAD"), method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (!world.isClient() && SimpleVeinminer.playersVeinMining.contains((ServerPlayerEntity) player)) {
            if (SimpleVeinminer.canVeinmine(player, world, pos, state)) veinMine(world, pos, state, player);

        }
    }

    private void veinMine(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Item hand = player.getMainHandStack().getItem();

        double exhaustion = SimpleVeinminer.CONFIG.getValue("server.exhaustion.exhaustion_per_block_veinmined", Double.class);
        double damageMultiplier = SimpleVeinminer.CONFIG.getValue("server.durability.damage_multiplier", Double.class);
        double swordMultiplier = SimpleVeinminer.CONFIG.getValue("server.durability.sword_multiplier", Double.class);
        boolean consumeOnInstantBreak = SimpleVeinminer.CONFIG.getValue("server.durability.consume_on_instant_break", Boolean.class);
        boolean harderBlocksExhaustMore = SimpleVeinminer.CONFIG.getValue("server.exhaustion.harder_blocks_exhaust_more", Boolean.class);
        double hardnessExhaustionMultiplier = SimpleVeinminer.CONFIG.getValue("server.exhaustion.hardness_exhaustion_multiplier", Double.class);

        ArrayList<BlockPos> blocksToBreak = SimpleVeinminer.getBlocksToVeinmine(world, pos, state);

        for (int i = 1; i < blocksToBreak.size(); i++) {
            BlockPos currentPos = blocksToBreak.get(i);
            //world.breakBlock(currentPos, false);
            if (!player.isCreative()) Block.dropStacks(world.getBlockState(currentPos), world, pos, world.getBlockEntity(currentPos), player, player.getMainHandStack());
            world.removeBlock(currentPos, false);
        }
        Block block = state.getBlock();
        int veinMined = blocksToBreak.size() - 1;
        if (hand instanceof SwordItem) damageMultiplier *= swordMultiplier;
        if ((consumeOnInstantBreak || block.getHardness() > 0) && (hand instanceof ToolItem || hand instanceof SwordItem)) player.getMainHandStack().damage((int) (damageMultiplier * veinMined), player.getRandom(), (ServerPlayerEntity) player);
        double totalExhausted = (exhaustion + (harderBlocksExhaustMore ? (block.getHardness() * hardnessExhaustionMultiplier) : 0)) * veinMined;
        //SimpleVeinminer.LOGGER.info(totalExhausted + "");
        player.addExhaustion((float) totalExhausted);
    }
}
