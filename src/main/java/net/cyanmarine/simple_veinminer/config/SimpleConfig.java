package net.cyanmarine.simple_veinminer.config;

import com.oroarmor.config.*;
import me.shedaniel.math.Color;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.cyanmarine.simple_veinminer.client.SimpleVeinminerClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

public class SimpleConfig extends Config {
    public static final List<ConfigItemGroup> configs = List.of(new ServerGroup());

    public SimpleConfig() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "simpleveinminer.json"), "simple_veinminer");
    }

    public static class ServerGroup extends ConfigItemGroup {
        public static final IntegerConfigItem maxBlocks = new IntegerConfigItem("max_blocks_broken_when_veinmining", 150, "veinmine.config.max_blocks");

        public ServerGroup() {
            super(List.of(maxBlocks, new Durability(), new Restrictions(), new Exhaustion()), "server");
        }

        public static class Durability extends ConfigItemGroup {
            public static final DoubleConfigItem durabilityMultiplier = new DoubleConfigItem("damage_multiplier", 1.0, "veinmine.config.damage_multiplier");
            public static final DoubleConfigItem swordMultiplier = new DoubleConfigItem("sword_multiplier", 2.0, "veinmine.config.sword_multiplier");
            public static final BooleanConfigItem consumeOnInstantBreak = new BooleanConfigItem("consume_on_instant_break", false, "veinmine.config.consume_on_instant");

            public Durability() {
                super(List.of(durabilityMultiplier, swordMultiplier, consumeOnInstantBreak), "durability");
            }
        }

        public static class Restrictions extends ConfigItemGroup {
            public static final BooleanConfigItem emptyHand = new BooleanConfigItem("can_veinmine_with_empty_hand", true, "Allow veinmining without a tool (or sword)");
            public static final BooleanConfigItem canVeinMineHungry = new BooleanConfigItem("can_veinmine_hungry", false, "Allow veinmining even if the player is hungry");
            public static final BooleanConfigItem onlyOres = new BooleanConfigItem("only_ores", false, "Only veinmine ores");
            public static final BooleanConfigItem creativeBypass = new BooleanConfigItem("creative_bypass", true, "Creative mode bypasses restrictions");

            public Restrictions() {
                super(List.of(emptyHand, canVeinMineHungry, onlyOres, creativeBypass), "restrictions");
            }
        }

        public static class Exhaustion extends ConfigItemGroup{
            public static final DoubleConfigItem exhaustion = new DoubleConfigItem("exhaustion_per_block_veinmined", 0.3, "How much each block exhausts");
            public static final BooleanConfigItem harderBlocksExhaustMore = new BooleanConfigItem("harder_blocks_exhaust_more", true, "Harder blocks exhaust more");
            public static final DoubleConfigItem hardnessExhaustionMultiplier = new DoubleConfigItem("hardness_exhaustion_multiplier", 0.1, "Hardness weight");

            public Exhaustion() {
                super(List.of(exhaustion, harderBlocksExhaustMore, hardnessExhaustionMultiplier), "exhaustion");
            }
        }
    }
}