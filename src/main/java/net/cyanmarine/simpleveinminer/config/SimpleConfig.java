package net.cyanmarine.simpleveinminer.config;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleConfig extends Config implements ConfigContainer {
    @ConfigEntry
    public boolean placeInInventory = false;
    @ConfigEntry
    public boolean debug = false;
    @Transitive
    public Limits limits = new Limits();
    @Transitive
    public Restrictions restrictions = new Restrictions();
    @Transitive
    public Exhaustion exhaustion = new Exhaustion();
    @Transitive
    public Durability durability = new Durability();

    public SimpleConfig() {
        super(SimpleVeinminer.MOD_ID);
    }

    public static SimpleConfigCopy copy(PacketByteBuf buf) {
        return new SimpleConfigCopy(buf);
    }

    public void reset() {
        this.placeInInventory = false;
        this.limits = new Limits();
        this.restrictions = new Restrictions();
        this.exhaustion = new Exhaustion();
        this.durability = new Durability();
        syncConfig();
    }

    public static void syncConfig() {
        SimpleVeinminer.getConfig().save();
        SimpleVeinminer.SyncConfigForAllPlayers();
    }

    public void setPlaceInInventory(boolean placeInInventory) {
        this.placeInInventory = placeInInventory;
        syncConfig();
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
        syncConfig();
    }

    public PacketByteBuf WritePacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(restrictions.canVeinmineHungry);
        buf.writeBoolean(restrictions.canVeinmineWithEmptyHand);
        buf.writeBoolean(restrictions.creativeBypass);
        buf.writeBoolean(restrictions.canOnlyUseSuitableTools);
        buf.writeEnumConstant(restrictions.restrictionList.listType);
        buf.writeString(String.join(";", restrictions.restrictionList.list));

        buf.writeInt(limits.maxBlocks);
        buf.writeBoolean(limits.materialBasedLimits);

        return buf;
    }

    @ConfigEntries(includeAll = true)
    public static class Limits implements ConfigGroup {
        public int maxBlocks = 150;
        public boolean materialBasedLimits = false;
        @ConfigEntry.BoundedInteger(min = 1)
        public int radius = 1;
        public SPREAD_ACCURACY spreadAccuracy = SPREAD_ACCURACY.ACCURATE;

        public enum SPREAD_ACCURACY {
            KINDA_ACCURATE, ACCURATE
        }

        public void setMaxBlocks(int maxBlocks) {
            this.maxBlocks = maxBlocks;
            syncConfig();
        }

        public void setMaterialBasedLimits(boolean materialBasedLimits) {
            this.materialBasedLimits = materialBasedLimits;
            syncConfig();
        }

        public void setRadius(int radius) {
            if (radius < 1) radius = 1;
            this.radius = radius;
            syncConfig();
        }

        public void setAccurateSpread(SPREAD_ACCURACY spreadAccuracy) {
            this.spreadAccuracy = spreadAccuracy;
            syncConfig();
        }
    }

    @ConfigEntries(includeAll = true)
    public static class Restrictions implements ConfigGroup {
        public boolean canVeinmineHungry = false;
        public boolean canVeinmineWithEmptyHand = true;
        public boolean creativeBypass = false;
        public boolean keepToolFromBreaking = true;
        public boolean onlyBreakBottomBlockForChainReactions = false;
        @ConfigEntry(comment = "Will only allow to veinmine wood using an axe, dirt using a shovel, stone using a pickaxe, etc.")
        public boolean canOnlyUseSuitableTools = false;
        @Transitive
        public RestrictionList restrictionList = new RestrictionList();
        @Transitive
        public RestrictionTags restrictionTags = new RestrictionTags();

        public void setKeepToolFromBreaking(boolean keepToolFromBreaking) {
            this.keepToolFromBreaking = keepToolFromBreaking;
            syncConfig();
        }

        public void setCanVeinmineWithEmptyHand(boolean canVeinmineWithEmptyHand) {
            this.canVeinmineWithEmptyHand = canVeinmineWithEmptyHand;
            syncConfig();
        }

        public void setCreativeBypass(boolean creativeBypass) {
            this.creativeBypass = creativeBypass;
            syncConfig();
        }

        public void setCanVeinmineHungry(boolean canVeinmineHungry) {
            this.canVeinmineHungry = canVeinmineHungry;
            syncConfig();
        }

        public void setCanOnlyUseSuitableTools(boolean canOnlyUseSuitableTools) {
            this.canOnlyUseSuitableTools = canOnlyUseSuitableTools;
            syncConfig();
        }

        @ConfigEntries(includeAll = true)
        public static class RestrictionTags implements ConfigGroup {
            public boolean enabled = true;
            public List<String> tags = Arrays.asList("#c:.+_ores", "minecraft:(tall_)?grass", "minecraft:(large_)?fern");

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
                syncConfig();
            }

            public void setTags(List<String> tags) {
                this.tags = tags;
                syncConfig();
            }

            public void add(String value) {
                if (this.tags.contains(value)) return;
                // this.list.add(value); // Doesn't work for some reason
                ArrayList<String> newList = new ArrayList<>(this.tags);
                newList.add(value);
                this.setTags(newList);
                syncConfig();
            }

            public void remove(String value) {
                if (!this.tags.contains(value)) return;
                ArrayList<String> newList = new ArrayList<>(this.tags);
                newList.remove(value);
                this.setTags(newList);
                syncConfig();
            }

            public void clear() {
                this.tags = new ArrayList<>();
                syncConfig();
            }
        }

        @ConfigEntries(includeAll = false)
        public static class RestrictionList implements ConfigGroup {
            @ConfigEntry(comment = "Valid values are NONE, BLACKLIST, and WHITELIST")
            @ConfigEntry.Dropdown
            public ListType listType = ListType.NONE;
            @ConfigEntry(comment = "More information at https://github.com/PrincessCyanMarine/Simple-Veinminer/wiki/Whitelist-and-Blacklist")
            public List<String> list = Arrays.asList("#c:logs", "#c:ores");

            public void setList(List<String> list) {
                this.list = list;
                syncConfig();
            }

            public void add(String value) {
//                SimpleVeinminer.LOGGER.info(value);
                if (this.list.contains(value)) return;
                // this.list.add(value); // Doesn't work for some reason
                ArrayList<String> newList = new ArrayList<>(this.list);
                newList.add(value);
                this.setList(newList);
//                SimpleVeinminer.LOGGER.info(value);
                syncConfig();
            }

            public void remove(String value) {
                this.list.remove(value);
                syncConfig();
            }

            public void setListType(ListType listType) {
                this.listType = listType;
                syncConfig();
            }

            public enum ListType {
                NONE, WHITELIST, BLACKLIST
            }
        }

    }

    @ConfigEntries(includeAll = true)
    public static class Exhaustion implements ConfigGroup {
        public boolean exhaust = true;
        public double baseValue = 0.3;
        public boolean exhaustionBasedOnHardness = true;
        public double hardnessWeight = 0.1;

        public void setExhaust(boolean exhaust) {
            this.exhaust = exhaust;
            SimpleVeinminer.getConfig().save();
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
            SimpleVeinminer.getConfig().save();
        }

        public void setExhaustionBasedOnHardness(boolean exhaustionBasedOnHardness) {
            this.exhaustionBasedOnHardness = exhaustionBasedOnHardness;
            SimpleVeinminer.getConfig().save();
        }

        public void setHardnessWeight(double hardnessWeight) {
            this.hardnessWeight = hardnessWeight;
            SimpleVeinminer.getConfig().save();
        }
    }

    @ConfigEntries(includeAll = true)
    public static class Durability implements ConfigGroup {
        public double damageMultiplier = 1.0;
        public double swordMultiplier = 2.0;
        public boolean consumeOnInstantBreak = false;

        public void setDamageMultiplier(double damageMultiplier) {
            this.damageMultiplier = damageMultiplier;
            SimpleVeinminer.getConfig().save();
        }

        public void setSwordMultiplier(double swordMultiplier) {
            this.swordMultiplier = swordMultiplier;
            SimpleVeinminer.getConfig().save();
        }

        public void setConsumeOnInstantBreak(boolean consumeOnInstantBreak) {
            this.consumeOnInstantBreak = consumeOnInstantBreak;
            SimpleVeinminer.getConfig().save();
        }
    }

    public static class SimpleConfigCopy {
        public Restrictions restrictions = new Restrictions();
        public Limits limits = new Limits();

        private SimpleConfigCopy() {
        }

        SimpleConfigCopy(PacketByteBuf buf) {
            this.restrictions.canVeinmineHungry = buf.readBoolean();
            this.restrictions.canVeinmineWithEmptyHand = buf.readBoolean();
            this.restrictions.creativeBypass = buf.readBoolean();
            this.restrictions.canOnlyUseSuitableTools = buf.readBoolean();
            this.restrictions.restrictionList.listType = buf.readEnumConstant(Restrictions.RestrictionList.ListType.class);
            this.restrictions.restrictionList.list = Arrays.stream(buf.readString().split(";")).toList();

            this.limits.maxBlocks = buf.readInt();
            this.limits.materialBasedLimits = buf.readBoolean();
        }

        public static SimpleConfigCopy from(SimpleConfig config) {
            SimpleConfigCopy res = new SimpleConfigCopy();

            res.restrictions = config.restrictions;
            res.limits = config.limits;

            return res;
        }
    }
}
