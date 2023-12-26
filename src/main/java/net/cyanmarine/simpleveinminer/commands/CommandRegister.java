package net.cyanmarine.simpleveinminer.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.lortseam.completeconfig.data.Entry;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.cyanmarine.simpleveinminer.commands.argumenttypes.BlockIdOrTagArgumentType;
import net.cyanmarine.simpleveinminer.commands.argumenttypes.RestrictionListArgumentType;
import net.cyanmarine.simpleveinminer.config.SimpleConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegister {
    public CommandRegister() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("veinmining")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(
                            literal("placeInInventory").then(
                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                        boolean placeInInventory = BoolArgumentType.getBool(context, "value");
                                        SimpleVeinminer.getConfig().setPlaceInInventory(placeInInventory);
                                        context.getSource().sendMessage(Text.of("Veinmining \"place in inventory\" set to " + (placeInInventory ? "true" : "false")));
                                        return 1;
                                    })
                            ).executes(context -> {
                                boolean placeInInventory = !SimpleVeinminer.getConfig().placeInInventory;
                                context.getSource().sendMessage(Text.of(placeInInventory ? "Dropping blocks on the ground" : "Placing blocks in inventory"));
                                return 1;
                            })
                    ).then(
                            literal("limits").then(
                                    literal("maxBlocks")
                                            .then(
                                                    CommandManager.argument("value", IntegerArgumentType.integer(1, 10000))
                                                            .executes((context) -> {
                                                                int maxBlocks = IntegerArgumentType.getInteger(context, "value");
                                                                SimpleVeinminer.getConfig().limits.setMaxBlocks(maxBlocks);
                                                                context.getSource().sendMessage(Text.of("Veinmining \"max blocks\" set to " + maxBlocks));
                                                                return 1;
                                                            })
                                            ).executes((context) -> {
                                                int maxBlocks = SimpleVeinminer.getConfig().limits.maxBlocks;
                                                context.getSource().sendMessage(Text.of("Veinmining \"max blocks\" is " + maxBlocks));
                                                return 1;
                                            })
                            ).then(
                                    literal("materialBasedLimits").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean materialBasedLimits = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().limits.setMaterialBasedLimits(materialBasedLimits);
                                                context.getSource().sendMessage(Text.of("Veinmining \"material based limits\" set to " + (materialBasedLimits ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        boolean materialBasedLimits = SimpleVeinminer.getConfig().limits.materialBasedLimits;
                                        context.getSource().sendMessage(Text.of("Max vein size is " + (materialBasedLimits ? "" : "not ") + "material based"));
                                        return 1;
                                    })
                            )
                    ).then(
                            literal("restrictions").then(
                                    literal("hungry").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean canVeinmineHungry = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().restrictions.setCanVeinmineHungry(canVeinmineHungry);
                                                context.getSource().sendMessage(Text.of("Veinmining \"can veinmine hungry\" set to " + (canVeinmineHungry ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        boolean canVeinmineHungry = SimpleVeinminer.getConfig().restrictions.canVeinmineHungry;
                                        context.getSource().sendMessage(Text.of(canVeinmineHungry ? "Able to veinmine while hungry" : "Unable to veinmine while hungry"));
                                        return 1;
                                    })
                            ).then(
                                    literal("emptyHand").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean canVeinmineWithEmptyHand = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().restrictions.setCanVeinmineWithEmptyHand(canVeinmineWithEmptyHand);
                                                context.getSource().sendMessage(Text.of("Veinmining \"can veinmine with empty hand\" set to " + (canVeinmineWithEmptyHand ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        boolean canVeinmineWithEmptyHand = SimpleVeinminer.getConfig().restrictions.canVeinmineWithEmptyHand;
                                        context.getSource().sendMessage(Text.of(canVeinmineWithEmptyHand ? "Able to veinmine with empty hand" : "Unable to veinmine with empty hand"));
                                        return 1;
                                    })
                            ).then(
                                    literal("creativeBypass").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean creativeBypass = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().restrictions.setCreativeBypass(creativeBypass);
                                                context.getSource().sendMessage(Text.of("Veinmining \"creative bypass\" set to " + (creativeBypass ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context->{
                                        boolean creativeBypass = SimpleVeinminer.getConfig().restrictions.creativeBypass;
                                        context.getSource().sendMessage(Text.of("Creative bypass " + (creativeBypass ? "enabled" : "disabled")));
                                        return 1;
                                    })
                            ).then(
                                    literal("onlySuitableTools").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean onlySuitableTools = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().restrictions.setCanOnlyUseSuitableTools(onlySuitableTools);
                                                context.getSource().sendMessage(Text.of("Veinmining \"only suitable tools\" set to " + (onlySuitableTools ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context-> {
                                        boolean onlySuitableTools = SimpleVeinminer.getConfig().restrictions.canOnlyUseSuitableTools;
                                        context.getSource().sendMessage(Text.of(onlySuitableTools ? "Only suitable tools can be used" : "Any tool can be used"));
                                        return 1;
                                    })
                            ).then(
                                    literal("list").then(
                                            literal("type").then(
                                                    literal("whitelist").executes((context) -> {
                                                        SimpleVeinminer.getConfig().restrictions.restrictionList.setListType(SimpleConfig.Restrictions.RestrictionList.ListType.WHITELIST);
                                                        context.getSource().sendMessage(Text.of("Veinmining \"list type\" set to \"whitelist\""));
                                                        return 1;
                                                    })
                                            ).then(
                                                    literal("blacklist").executes((context) -> {
                                                        SimpleVeinminer.getConfig().restrictions.restrictionList.setListType(SimpleConfig.Restrictions.RestrictionList.ListType.BLACKLIST);
                                                        context.getSource().sendMessage(Text.of("Veinmining \"list type\" set to \"blacklist\""));
                                                        return 1;
                                                    })
                                            ).then(
                                                    literal("none").executes((context) -> {
                                                        SimpleVeinminer.getConfig().restrictions.restrictionList.setListType(SimpleConfig.Restrictions.RestrictionList.ListType.NONE);
                                                        context.getSource().sendMessage(Text.of("Veinmining \"list type\" set to \"none\""));
                                                        return 1;
                                                    })
                                            ).executes(context -> {
                                                SimpleConfig.Restrictions.RestrictionList.ListType listType = SimpleVeinminer.getConfig().restrictions.restrictionList.listType;
                                                context.getSource().sendMessage(Text.of("Veinmining \"list type\" is set to \"" + listType.name().toUpperCase() + "\""));
                                                return 1;
                                            })
                                    ).then(
                                            literal("add").then(
                                                    argument("value", BlockIdOrTagArgumentType.blockIdOrTag(registryAccess))
                                                            .executes((context) -> {
                                                                String newValue = BlockIdOrTagArgumentType.getBlockIdOrTag(context, "value");
                                                                SimpleVeinminer.getConfig().restrictions.restrictionList.add(newValue);
                                                                context.getSource().sendMessage(Text.of("Added " + newValue + " to list"));
                                                                return 1;
                                                            })
                                            )
                                    ).then(
                                            literal("remove").then(
                                                    argument("value", RestrictionListArgumentType.listItem())
                                                            .executes((context) -> {
                                                                String newValue = RestrictionListArgumentType.getListMember(context, "value");
                                                                SimpleVeinminer.getConfig().restrictions.restrictionList.remove(newValue);
                                                                context.getSource().sendMessage(Text.of("Removed " + newValue + " from \"" + SimpleVeinminer.getConfig().restrictions.restrictionList.listType.toString().toLowerCase() + "\" list"));
                                                                return 1;
                                                            })
                                            )
                                    ).then(
                                            literal("clear").executes((context) -> {
                                                SimpleVeinminer.getConfig().restrictions.restrictionList.setList(new ArrayList<>());
                                                context.getSource().sendMessage(Text.of("Cleared list"));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        List<String> list = SimpleVeinminer.getConfig().restrictions.restrictionList.list;

                                        if (list.size() == 0) {
                                            context.getSource().sendMessage(Text.of("List is empty"));
                                            context.getSource().sendMessage(Text.of("To add blocks, use \"/veinminer restrictions list add <id/tag>\""));
                                            context.getSource().sendMessage(Text.of("To remove blocks, use \"/veinminer restrictions list remove <id/tag>\""));
                                            context.getSource().sendMessage(Text.of("To clear the list, use \"/veinminer restrictions list clear\""));
                                            return 1;
                                        } else {
                                            context.getSource().sendMessage(Text.of("List type is set to " + SimpleVeinminer.getConfig().restrictions.restrictionList.listType.name().toUpperCase()));
                                            context.getSource().sendMessage(Text.of("List:"));
                                            list.forEach(s -> context.getSource().sendMessage(Text.of(s)));
                                        }
                                        return 1;
                                    })
                            )
                    ).then(
                            literal("exhaustion").then(
                                    literal("exhaust").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean exhaust = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().exhaustion.setExhaust(exhaust);
                                                context.getSource().sendMessage(Text.of("Veinmining \"exhaust\" set to " + (exhaust ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        boolean exhaust = SimpleVeinminer.getConfig().exhaustion.exhaust;
                                        context.getSource().sendMessage(Text.of("Veinmining exhaustion is " + (exhaust ? "enabled" : "disabled")));
                                        return 1;
                                    })
                            ).then(
                                    literal("baseValue").then(
                                            argument("value", DoubleArgumentType.doubleArg()).executes((context) -> {
                                                double baseValue = DoubleArgumentType.getDouble(context, "value");
                                                SimpleVeinminer.getConfig().exhaustion.setBaseValue(baseValue);
                                                context.getSource().sendMessage(Text.of("Veinmining \"base value\" set to " + baseValue));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        double baseValue = SimpleVeinminer.getConfig().exhaustion.baseValue;
                                        context.getSource().sendMessage(Text.of("Exhaustion \"base value\" is set to " + baseValue));
                                        return 1;
                                    })
                            ).then(
                                    literal("multiplyByHardness").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean multiplyByHardness = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().exhaustion.setExhaustionBasedOnHardness(multiplyByHardness);
                                                context.getSource().sendMessage(Text.of("Veinmining \"multiply by hardness\" set to " + (multiplyByHardness ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        boolean multiplyByHardness = SimpleVeinminer.getConfig().exhaustion.exhaustionBasedOnHardness;
                                        context.getSource().sendMessage(Text.of("Exhaustion is " + (multiplyByHardness ? "": "not ") + "hardness based"));
                                        return 1;
                                    })
                            ).then(
                                    literal("hardnessWeight").then(
                                            argument("value", DoubleArgumentType.doubleArg()).executes((context) -> {
                                                double hardnessMultiplier = DoubleArgumentType.getDouble(context, "value");
                                                SimpleVeinminer.getConfig().exhaustion.setHardnessWeight(hardnessMultiplier);
                                                context.getSource().sendMessage(Text.of("Veinmining \"hardness multiplier\" set to " + hardnessMultiplier));
                                                return 1;
                                            })
                                    ).executes(context->{
                                        double hardnessMultiplier = SimpleVeinminer.getConfig().exhaustion.hardnessWeight;
                                        context.getSource().sendMessage(Text.of("The hardness weight on exhaustion is " + hardnessMultiplier));
                                        return 1;
                                    })
                            )
                    ).then(
                            literal("durability").then(
                                    literal("multiplier").then(
                                            argument("value", DoubleArgumentType.doubleArg()).executes((context) -> {
                                                double multiplier = DoubleArgumentType.getDouble(context, "value");
                                                SimpleVeinminer.getConfig().durability.setDamageMultiplier(multiplier);
                                                context.getSource().sendMessage(Text.of("Veinmining \"durability multiplier\" set to " + multiplier));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        double multiplier = SimpleVeinminer.getConfig().durability.damageMultiplier;
                                        context.getSource().sendMessage(Text.of("Veinmining \"durability multiplier\" is set to " + multiplier));
                                        return 1;
                                    })
                            ).then(
                                    literal("swordMultiplier").then(
                                            argument("value", DoubleArgumentType.doubleArg()).executes((context) -> {
                                                double multiplier = DoubleArgumentType.getDouble(context, "value");
                                                SimpleVeinminer.getConfig().durability.setSwordMultiplier(multiplier);
                                                context.getSource().sendMessage(Text.of("Veinmining \"sword multiplier\" set to " + multiplier));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        double multiplier = SimpleVeinminer.getConfig().durability.swordMultiplier;
                                        context.getSource().sendMessage(Text.of("Veinmining \"sword multiplier\" is set to " + multiplier));
                                        return 1;
                                    })
                            ).then(
                                    literal("consumeOnInstantBreak").then(
                                            argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                boolean consumeOnInstantBreak = BoolArgumentType.getBool(context, "value");
                                                SimpleVeinminer.getConfig().durability.setConsumeOnInstantBreak(consumeOnInstantBreak);
                                                context.getSource().sendMessage(Text.of("Veinmining \"consume on instant break\" set to " + (consumeOnInstantBreak ? "true" : "false")));
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        boolean consumeOnInstantBreak = SimpleVeinminer.getConfig().durability.consumeOnInstantBreak;
                                        context.getSource().sendMessage(Text.of("Veinmining \"consume on instant break\" is " + (consumeOnInstantBreak ? "enabled" : "disabled")));
                                        return 1;
                                    })
                            )
                    ).then(
                            literal("reset").executes(context-> {
                                SimpleVeinminer.getConfig().reset();
                                context.getSource().sendMessage(Text.of("Resetting config to default values"));
                                return 1;
                            })
                    )
            );
        });
    }
}
