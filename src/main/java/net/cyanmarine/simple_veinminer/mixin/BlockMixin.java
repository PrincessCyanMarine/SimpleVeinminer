package net.cyanmarine.simple_veinminer.mixin;

import net.cyanmarine.simple_veinminer.SimpleConfig;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
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
        if (world.isClient()) return;
        if (SimpleVeinminer.playersVeinMining.contains((ServerPlayerEntity) player)) {
            boolean canVeinmineHungry = SimpleVeinminer.CONFIG.getValue("simple_veinminer.can_veinmine_hungry", Boolean.class);

            if (!canVeinmineHungry && player.getHungerManager().getFoodLevel() < 1)
                player.sendMessage(new LiteralText("You are too hungry to veinmine"), true);
            else
                veinMine(world, pos, state, player);
        }
    }

    private void veinMine(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Block compareTo = state.getBlock();
        ArrayList<BlockPos> blocksToBreak  = new ArrayList<>();
        blocksToBreak.add(pos);
        int maxBlocks = SimpleVeinminer.CONFIG.getValue("simple_veinminer.max_blocks_broken_when_veinmining", Integer.class);
        double exhaustion = SimpleVeinminer.CONFIG.getValue("simple_veinminer.exhaustion_per_block_veinmined", Double.class);
        double damageMultiplier = SimpleVeinminer.CONFIG.getValue("simple_veinminer.damage_multiplier", Double.class);
        double swordMultiplier = SimpleVeinminer.CONFIG.getValue("simple_veinminer.sword_multiplier", Double.class);
        boolean consumeOnInstantBreak = SimpleVeinminer.CONFIG.getValue("simple_veinminer.consume_on_instant_break", Boolean.class);
        boolean harderBlocksExhaustMore = SimpleVeinminer.CONFIG.getValue("simple_veinminer.harder_blocks_exhaust_more", Boolean.class);
        double hardnessExhaustionMultiplier = SimpleVeinminer.CONFIG.getValue("simple_veinminer.hardness_exhaustion_multiplier", Double.class);

        int i, x, y, z;
        for (i = 0; i < maxBlocks && i < blocksToBreak.size(); i++) {
            BlockPos currentPos = blocksToBreak.get(i);
            for (x = -1; x <= 1; x++)
                for (y = -1; y <= 1; y++)
                    for (z = -1; z <= 1; z++) {
                        if (blocksToBreak.size() >= maxBlocks) break;
                        BlockPos testPos = currentPos.add(x, y, z);
                        if (blocksToBreak.size() < maxBlocks && !blocksToBreak.contains(testPos) && world.getBlockState(testPos).getBlock().equals(compareTo)) blocksToBreak.add(testPos);
                    }
        }

        for (i = 1; i < blocksToBreak.size(); i++) {
            BlockPos currentPos = blocksToBreak.get(i);
            //world.breakBlock(currentPos, false);
            world.removeBlock(currentPos, false);
            if (!player.isCreative()) Block.dropStacks(state, world, pos, world.getBlockEntity(currentPos), player, player.getMainHandStack());
        }
        Block block = state.getBlock();
        int veinMined = blocksToBreak.size() - 1;
        if (player.getMainHandStack().getItem() instanceof SwordItem) damageMultiplier *= swordMultiplier;
        if (consumeOnInstantBreak || block.getHardness() > 0) player.getMainHandStack().damage((int) (damageMultiplier * veinMined), player.getRandom(), (ServerPlayerEntity) player);
        double totalExhausted = (exhaustion + (harderBlocksExhaustMore ? (block.getHardness() * hardnessExhaustionMultiplier) : 0)) * veinMined;
        SimpleVeinminer.LOGGER.info(totalExhausted + "");
        player.addExhaustion((float) totalExhausted);
    }
}
