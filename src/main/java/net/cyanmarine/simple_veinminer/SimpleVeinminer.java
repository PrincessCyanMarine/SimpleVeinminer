package net.cyanmarine.simple_veinminer;

import com.oroarmor.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class SimpleVeinminer implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner");
    public static ArrayList<ServerPlayerEntity> playersVeinMining;
    public static Config CONFIG = new SimpleConfig();

    @Override
    public void onInitialize() {
        CONFIG.readConfigFromFile();
        CONFIG.saveConfigToFile();
        playersVeinMining = new ArrayList<>();
        ServerPlayNetworking.registerGlobalReceiver(Constants.NETWORKING_VEINMINE.identifier, (server, player, handler, buf, sender) -> {
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
