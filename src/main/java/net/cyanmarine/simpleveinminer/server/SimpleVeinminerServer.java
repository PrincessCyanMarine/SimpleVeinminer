package net.cyanmarine.simpleveinminer.server;

import net.cyanmarine.simpleveinminer.config.SimpleConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.SERVER)
public class SimpleVeinminerServer implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner - Server");
    private static SimpleConfig config;

    public static SimpleConfig getConfig() {
        return config;
    }

    @Override
    public void onInitializeServer() {
        config = new SimpleConfig();
        config.load();


        LOGGER.info("Simple veinminer initialized");
    }
}
