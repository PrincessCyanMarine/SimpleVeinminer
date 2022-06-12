package net.cyanmarine.simple_veinminer.server;

import net.cyanmarine.simple_veinminer.Constants;
import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.SERVER)
public class SimpleVeinminerServer implements DedicatedServerModInitializer {
    private static SimpleConfig config;
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner - Server");

    @Override
    public void onInitializeServer() {
        config = new SimpleConfig();
        config.load();


        LOGGER.info("Simple veinminer initialized");
    }

    public static SimpleConfig getConfig() {
        return config;
    }
}
