package net.cyanmarine.simple_veinminer.mixin;

import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At("HEAD"), method = "onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (!world.isClient() && SimpleVeinminer.isVeinmining(player)) {
            if (SimpleVeinminer.canVeinmine(player, world, pos, state, SimpleVeinminer.getConfig().restrictions))
                veinMine(world, pos, state, player);
        }
    }

    private void veinMine(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemStack hand = player.getMainHandStack();
        Item handItem = player.getMainHandStack().getItem();

        SimpleConfig config = SimpleVeinminer.getConfig();

        SimpleConfig.Exhaustion exhaustion = config.exhaustion;
        // SimpleVeinminer.LOGGER.info(exhaustion.baseValue + " " + exhaustion.hardnessWeight);
        SimpleConfig.Durability durability = config.durability;
        double damageMultiplier = durability.damageMultiplier;
        if (handItem instanceof SwordItem) damageMultiplier *= durability.swordMultiplier;

        ArrayList<BlockPos> blocksToBreak = SimpleVeinminer.getBlocksToVeinmine(pos, state, SimpleVeinminer.getMaxBlocks(handItem), player);

        Block block = state.getBlock();
        double totalExhausted = (exhaustion.baseValue + (exhaustion.exhaustionBasedOnHardness ? (block.getHardness() * exhaustion.hardnessWeight) : 0));

        for (int i = 0; i < blocksToBreak.size(); i++) {
            BlockPos currentPos = blocksToBreak.get(i);
            BlockState currentState = world.getBlockState(currentPos);
            if (currentState.isAir()) continue;
            Block currentBlock = currentState.getBlock();
            BlockEntity currentBlockEntity = world.getBlockEntity(currentPos);
            //world.breakBlock(currentPos, false);
            if (shouldDrop(player, hand, state)) currentBlock.dropStacks(currentState, world, pos, currentBlockEntity, player, hand);
            player.incrementStat(Stats.MINED.getOrCreateStat(currentBlock));
            world.removeBlock(currentPos, false);

            if (shouldDamage(player, hand, currentBlock, durability)) hand.damage((int) (damageMultiplier), player.getRandom(), (ServerPlayerEntity) player);
            if (exhaustion.exhaust)
                player.addExhaustion((float) totalExhausted);

            if (hand.isDamageable() &&  hand.getDamage() >= hand.getMaxDamage()) break;
        }
    }

    public boolean shouldDrop(PlayerEntity player, ItemStack hand, BlockState state) {
        Material material = state.getMaterial();
        return !player.isCreative() && (!(material == Material.METAL || material == Material.STONE || state.isOf(Blocks.SNOW)) || hand.isSuitableFor(state));
    }

    public boolean shouldDamage(PlayerEntity player, ItemStack hand, Block block, SimpleConfig.Durability durability) {
        return !player.isCreative() && hand.isDamageable() && (durability.consumeOnInstantBreak || block.getHardness() > 0);
    }
}