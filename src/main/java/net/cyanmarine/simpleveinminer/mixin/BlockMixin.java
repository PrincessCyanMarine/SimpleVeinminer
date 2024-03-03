package net.cyanmarine.simpleveinminer.mixin;

import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.cyanmarine.simpleveinminer.config.SimpleConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow
    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack) {
        return null;
    }

    @Shadow
    public static void dropStack(World world, BlockPos pos, ItemStack stack) {
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        if (!world.isClient() && SimpleVeinminer.isVeinmining(player)) {
            if (SimpleVeinminer.canVeinmine(player, world, pos, state, SimpleVeinminer.getConfig().restrictions))
                veinMine(world, pos, state, player);
        }
    }

    @Unique
    private void veinMine(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemStack hand = player.getMainHandStack();
        Item handItem = player.getMainHandStack().getItem();

        SimpleConfig config = SimpleVeinminer.getConfig();

        SimpleConfig.Exhaustion exhaustion = config.exhaustion;
        // SimpleVeinminer.LOGGER.info(exhaustion.baseValue + " " + exhaustion.hardnessWeight);
        SimpleConfig.Durability durability = config.durability;
        double damageMultiplier = durability.damageMultiplier;
        if (isItemSpecial(handItem)) damageMultiplier *= durability.swordMultiplier;
        boolean debug = SimpleVeinminer.isDebug();


        /* Damage limit while veinmining
        Keep in mind that the damage of the first block is calculated *after* breaking every other block.
        The damage dealt by Block 0 is the default vanilla value (1 for items & 2 for sword/tridents).

        Meaning of maxAllowedDamage's value: We should veinmine while the durability is greater than damageMultiplier
        (and so the durability for breaking Block 0 is at least 1 when we finish veinmining)
        */
        int maxAllowedDamage = hand.getMaxDamage() - (int) damageMultiplier;

        ArrayList<BlockPos> blocksToBreak = SimpleVeinminer.getBlocksToVeinmine(pos, state, SimpleVeinminer.getMaxBlocks(handItem), SimpleVeinminer.getVeinminingRadius(player), SimpleVeinminer.getSpreadAccuracy(), player, debug, config.restrictions.onlyBreakBottomBlockForChainReactions);
        if (debug) world.setBlockState(pos, Blocks.BEDROCK.getDefaultState());

        // Skip Block 0 as the player breaks this block directly
        for (BlockPos currentPos : blocksToBreak.subList(1, blocksToBreak.size())) {
            BlockState currentState = world.getBlockState(currentPos);
            Block currentBlock = currentState.getBlock();

            // Check if we can veinmine blocks
            boolean doDamage = shouldDamage(player, hand, currentBlock, durability);
//            SimpleVeinminer.LOGGER.info(hand.getDamage() + "/" + hand.getMaxDamage());
            if (doDamage && (hand.getDamage() >= maxAllowedDamage || (config.restrictions.keepToolFromBreaking && hand.getDamage() >= maxAllowedDamage - 1)))
                break;

            // Do veinmining
            if (currentState.isAir()) continue;
            if (debug) {
                world.setBlockState(currentPos, Blocks.BEDROCK.getDefaultState());
                continue;
            }
            BlockEntity currentBlockEntity = world.getBlockEntity(currentPos);
            //world.breakBlock(currentPos, false);

            boolean willDrop = shouldDrop(player, hand, currentState);
            List<ItemStack> stacks = getDroppedStacks(currentState, (ServerWorld) world, currentPos, currentBlockEntity, player, hand);
            if (willDrop && stacks != null)
                stacks.forEach((stack) -> {
                    if (SimpleVeinminer.getConfig().placeInInventory) player.getInventory().offerOrDrop(stack);
                    else dropStack(world, pos, stack);
                });
            currentState.onStacksDropped((ServerWorld) world, currentPos, hand, willDrop);
            // currentBlock.dropStacks(currentState, world, pos, currentBlockEntity, player, hand);
            if (willDrop && (stacks == null || stacks.isEmpty()) && currentState.isOf(Blocks.ICE) && !world.getBlockState(pos.down()).isAir())
                world.setBlockState(currentPos, Blocks.WATER.getDefaultState());
            else world.removeBlock(currentPos, false);

            player.incrementStat(Stats.MINED.getOrCreateStat(currentBlock));


            if (doDamage)
                hand.damage((int) (damageMultiplier), player.getRandom(), (ServerPlayerEntity) player);

            if (exhaustion.exhaust) {
                double totalExhausted = (exhaustion.baseValue + (exhaustion.exhaustionBasedOnHardness ? (currentBlock.getHardness() * exhaustion.hardnessWeight) : 0));
                player.addExhaustion((float) totalExhausted);
            }
        }
    }

    @Unique
    public boolean shouldDrop(PlayerEntity player, ItemStack hand, BlockState state) {
        return !player.isCreative() && (!state.isToolRequired() || hand.isSuitableFor(state));
    }

    @Unique
    public boolean shouldDamage(PlayerEntity player, ItemStack hand, Block block, SimpleConfig.Durability durability) {
        Item handItem = hand.getItem();
        return !player.isCreative() && hand.isDamageable() && (durability.consumeOnInstantBreak || block.getHardness() > 0) && (
                isItemSpecial(handItem) ||
                        handItem instanceof MiningToolItem ||
                        handItem instanceof ShearsItem
        );
    }

    /** Compare item to the items that get damaged by 2 when breaking blocks**/
    @Unique
    public boolean isItemSpecial(Item item){
        return item instanceof SwordItem ||
                item instanceof TridentItem;
    }
}