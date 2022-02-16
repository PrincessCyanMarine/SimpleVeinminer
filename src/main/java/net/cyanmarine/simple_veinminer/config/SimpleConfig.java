package net.cyanmarine.simple_veinminer.config;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;

import java.util.Arrays;
import java.util.List;

public class SimpleConfig extends Config implements ConfigContainer {
    @Transitive
    public Restrictions restrictions = new Restrictions();
    @Transitive
    public Exhaustion exhaustion = new Exhaustion();
    @Transitive
    public Durability durability = new Durability();
    //@Transitive
    //public RestrictionList restrictionList = new RestrictionList();

    public SimpleConfig() {
        super("simple_veinminer");
    }

    @ConfigEntry
    public int maxBlocks = 150;

    @ConfigEntries
    public static class Restrictions implements ConfigGroup {
        public boolean canVeinmineHungry = false;
        public boolean canVeinmineWithEmptyHand = true;
        public boolean creativeBypass = true;

        @Transitive
        public RestrictionList restrictionList = new RestrictionList();

        @ConfigEntries
        public static class RestrictionList implements ConfigGroup {
            @ConfigEntry.Dropdown
            public ListType listType = ListType.NONE;
            public List<String> list = List.of("#minecraft:logs", "#c:ores");

            public static enum ListType {
                NONE, WHITELIST, BLACKLIST;
            }
        }

    }

    @ConfigEntries
    public static class Exhaustion implements ConfigGroup {
        public double baseValue = 0.3;
        public boolean exhaustionBasedOnHardness = true;
        public double hardnessWeight = 0.1;
    }

    @ConfigEntries
    public static class Durability implements ConfigGroup {
        public double damageMultiplier = 1.0;
        public double swordMultiplier = 2.0;
        public boolean consumeOnInstantBreak = false;
    }
}
