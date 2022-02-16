package net.cyanmarine.simple_veinminer;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.command.ConfigCommand;
import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleVeinminer implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner");
    public static ArrayList<ServerPlayerEntity> playersVeinMining;
    public static Config CONFIG = new SimpleConfig();

    public static ArrayList<BlockPos> getBlocksToVeinmine(World world, BlockPos pos, BlockState state) {
        int maxBlocks = SimpleVeinminer.CONFIG.getValue("server.max_blocks_broken_when_veinmining", Integer.class);
        Block compareTo = state.getBlock();

        ArrayList<BlockPos> blocksToBreak  = new ArrayList<>();
        blocksToBreak.add(pos);

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

        return blocksToBreak;
    }

    public static boolean canVeinmine(PlayerEntity player, World world, BlockPos pos, BlockState state) {
        boolean creativeBypass = SimpleVeinminer.CONFIG.getValue("server.restrictions.creative_bypass", Boolean.class);
        if (creativeBypass && player.isCreative()) return true;

        boolean canVeinmineHungry = SimpleVeinminer.CONFIG.getValue("server.restrictions.can_veinmine_hungry", Boolean.class);
        boolean canVeinmineWithemptyHand = SimpleVeinminer.CONFIG.getValue("server.restrictions.can_veinmine_with_empty_hand", Boolean.class);
        boolean onlyOres = SimpleVeinminer.CONFIG.getValue("server.restrictions.only_ores", Boolean.class);
        Item hand = player.getMainHandStack().getItem();

        if (onlyOres && !state.isIn(BlockTags.getTagGroup().getTag(new Identifier("c:ores")))) return false;

        if (!canVeinmineWithemptyHand && !(hand instanceof ToolItem || hand instanceof SwordItem || hand instanceof ShearsItem)) {
            player.sendMessage(new TranslatableText("veinmine.message.restriction.tool"), true);
            return false;
        }

        if (!canVeinmineHungry && player.getHungerManager().getFoodLevel() < 1) {
            player.sendMessage(new TranslatableText("veinmine.message.restriction.hungry"), true);
            return false;
        }

        return true;
    }

    @Override
    public void onInitialize() {
        CONFIG.readConfigFromFile();
        CONFIG.saveConfigToFile();

        ServerLifecycleEvents.SERVER_STOPPED.register(instance -> CONFIG.saveConfigToFile());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CONFIG.readConfigFromFile();
            CONFIG.saveConfigToFile();
        });

        // CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> new ConfigCommand<ServerCommandSource>(CONFIG).register(dispatcher, p -> p.hasPermissionLevel(2)));

        playersVeinMining = new ArrayList<>();
        ServerPlayNetworking.registerGlobalReceiver(Constants.NETWORKING_VEINMINE, (server, player, handler, buf, sender) -> {
            boolean isVeinMining = buf.readBoolean();
            //LOGGER.info(isVeinMining ? "true" : "false");
            if (isVeinMining)
                playersVeinMining.add(player);
            else
                playersVeinMining.remove(player);
        });
        LOGGER.info("Simple VeinMiner initialized");
    }
}
