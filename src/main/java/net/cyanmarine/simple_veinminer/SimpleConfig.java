package net.cyanmarine.simple_veinminer;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimpleConfig extends Config {
    public static final List<ConfigItemGroup> configs = List.of(new MainGroup());

    public SimpleConfig() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "simpleveinminer.json"), "simple_veinminer");
    }

    public static class MainGroup extends ConfigItemGroup {
        public static final ConfigItem<Integer> maxBlocks = new ConfigItem<>("max_blocks_broken_when_veinmining", 150, "How many blocks can be veinmined");
        public static final ConfigItem<Double> exhaustion = new ConfigItem<>("exhaustion_per_block_veinmined", 0.3, "How much each block exhausts");
        public static final ConfigItem<Double> durabilityMultiplier = new ConfigItem<>("damage_multiplier", 1.0, "How much durability does each block consume");
        public static final ConfigItem<Double> swordMultiplier = new ConfigItem<>("sword_multiplier", 2.0, "How much extra durability does each block consume from swords");
        public static final ConfigItem<Boolean> consumeOnInstantBreak = new ConfigItem<>("consume_on_instant_break", false, "Consume durability on blocks with 0 hardness");
        public static final ConfigItem<Boolean> canVeinMineHungry = new ConfigItem<>("can_veinmine_hungry", false, "Allow veinmining even if the player is hungry");
        public static final ConfigItem<Boolean> harderBlocksExhaustMore = new ConfigItem<>("harder_blocks_exhaust_more", true, "Harder blocks exhaust more");
        public static final ConfigItem<Double> hardnessExhaustionMultiplier = new ConfigItem<>("hardness_exhaustion_multiplier", 0.1, "Hardness weight");

        public MainGroup() {
            super(List.of(maxBlocks, exhaustion, durabilityMultiplier, swordMultiplier, consumeOnInstantBreak, canVeinMineHungry, harderBlocksExhaustMore, hardnessExhaustionMultiplier), "simple_veinminer");
        }
    }
}