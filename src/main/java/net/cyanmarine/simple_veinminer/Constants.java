package net.cyanmarine.simple_veinminer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class Constants {
    public static final Identifier NETWORKING_VEINMINE = new Identifier("networking.channel.veinmine");
    public static final Identifier CONFIG_SYNC = new Identifier("networking.channel.config_sync");
    public static final Identifier ASK_CONFIG_SYNC = new Identifier("networking.channel.ask_config_sync");
    public static final Identifier SERVERSIDE_UPDATE = new Identifier("networking.channel.serverside_update");
}
