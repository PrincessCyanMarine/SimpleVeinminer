package net.cyanmarine.simple_veinminer.server;

import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class SimpleVeinminerServer implements DedicatedServerModInitializer {
    private static SimpleConfig config;

    @Override
    public void onInitializeServer() {
        config = new SimpleConfig();
        config.load();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
        });
    }

    public static SimpleConfig getConfig() {
        return config;
    }
}
