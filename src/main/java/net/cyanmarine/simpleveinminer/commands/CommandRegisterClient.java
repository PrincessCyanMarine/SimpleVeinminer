package net.cyanmarine.simpleveinminer.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.shedaniel.math.Color;
import net.cyanmarine.simpleveinminer.client.SimpleVeinminerClient;
import net.cyanmarine.simpleveinminer.commands.argumenttypes.AnchorArgumentType;
import net.cyanmarine.simpleveinminer.commands.argumenttypes.HighlightModesArgumentType;
import net.cyanmarine.simpleveinminer.config.SimpleConfigClient;
import net.cyanmarine.simpleveinminer.config.enums.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegisterClient {
    public CommandRegisterClient() {
        SimpleVeinminerClient.LOGGER.info("Initializing client commands");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("veinminingclient").then(
                            literal("hud").then(
                                    literal("position").then(
                                            literal("x").then(
                                                    argument("value", IntegerArgumentType.integer())
                                                            .executes((context) -> {
                                                                int x = IntegerArgumentType.getInteger(context, "value");
                                                                SimpleVeinminerClient.getConfig().setHudX(x);
                                                                context.getSource().getPlayer().sendMessage(Text.of("Hud X position set to " + x));
                                                                return 1;
                                                            })
                                            ).executes(context -> {
                                                int x = SimpleVeinminerClient.getConfig().hudDisplay.x;
                                                context.getSource().getPlayer().sendMessage(Text.of("Hud position X is " + x));
                                                return 1;
                                            })
                                    ).then(
                                            literal("y").then(
                                                    argument("value", IntegerArgumentType.integer())
                                                            .executes((context) -> {
                                                                int y = IntegerArgumentType.getInteger(context, "value");
                                                                SimpleVeinminerClient.getConfig().setHudY(y);
                                                                context.getSource().getPlayer().sendMessage(Text.of("Hud Y position set to " + y));
                                                                return 1;
                                                            })
                                            ).executes(context -> {
                                                int y = SimpleVeinminerClient.getConfig().hudDisplay.y;
                                                context.getSource().getPlayer().sendMessage(Text.of("Hud position Y is " + y));
                                                return 1;
                                            })
                                    ).executes((context) -> {
                                        int x = SimpleVeinminerClient.getConfig().hudDisplay.x;
                                        int y = SimpleVeinminerClient.getConfig().hudDisplay.y;
                                        context.getSource().getPlayer().sendMessage(Text.of("Hud position is " + x + ", " + y));
                                        return 1;
                                    })
                            ).then(
                                    literal("anchor").then(
                                            literal("vertical").then(
                                                      argument("value", AnchorArgumentType.verticalAnchor())
                                                              .executes(context -> {
                                                                  VerticalAnchor anchor = AnchorArgumentType.getVerticalAnchor(context, "value");
                                                                  SimpleVeinminerClient.getConfig().setHudVerticalAnchor(anchor);
                                                                  context.getSource().getPlayer().sendMessage(Text.of("Hud vertical anchor set to " + anchor.asString()));
                                                                  return 1;
                                                              })
                                            ).executes(context -> {
                                                VerticalAnchor anchor = SimpleVeinminerClient.getConfig().hudDisplay.vertical_anchor;
                                                context.getSource().getPlayer().sendMessage(Text.of("Hud vertical anchor is set to " + anchor.asString()));
                                                return  1;
                                            })
                                    ).then(
                                            literal("horizontal").then(
                                                    argument("value", AnchorArgumentType.horizontalAnchor())
                                                            .executes(context -> {
                                                                HorizontalAnchor anchor = AnchorArgumentType.getHorizontalAnchor(context, "value");
                                                                SimpleVeinminerClient.getConfig().setHudHorizontalAnchor(anchor);
                                                                context.getSource().getPlayer().sendMessage(Text.of("Hud horizontal anchor set to " + anchor.asString()));
                                                                return 1;
                                                            })
                                            ).executes(context -> {
                                                HorizontalAnchor anchor = SimpleVeinminerClient.getConfig().hudDisplay.horizontal_anchor;
                                                context.getSource().getPlayer().sendMessage(Text.of("Hud horizontal anchor is set to " + anchor.asString()));
                                                return  1;
                                            })
                                    ).executes(context -> {
                                        VerticalAnchor vAnchor = SimpleVeinminerClient.getConfig().hudDisplay.vertical_anchor;
                                        HorizontalAnchor hAnchor = SimpleVeinminerClient.getConfig().hudDisplay.horizontal_anchor;
                                        context.getSource().getPlayer().sendMessage(Text.of("Hud anchors' values are:"));
                                        context.getSource().getPlayer().sendMessage(Text.of("    Vertical is set to " + vAnchor.asString()));
                                        context.getSource().getPlayer().sendMessage(Text.of("    Horizontal is set to " + hAnchor.asString()));
                                        return 1;
                                    })
                            ).then(
                                    literal("show").then(
                                            literal("block").then(
                                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                        boolean showBlock = BoolArgumentType.getBool(context, "value");
                                                        SimpleVeinminerClient.getConfig().setHudShowBlock(showBlock);
                                                        context.getSource().getPlayer().sendMessage(Text.of("Hud \"showBlock\" set to " + (showBlock ? "true" : "false")));
                                                        return 1;
                                                    })
                                            ).executes(context -> {
                                                boolean showBlock = SimpleVeinminerClient.getConfig().hudDisplay.showBlock;
                                                context.getSource().getPlayer().sendMessage(Text.of((showBlock ? "Showing block" : "Not showing block")));
                                                return 1;
                                            })
                                    ).then(
                                            literal("count").then(
                                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                        boolean showCount = BoolArgumentType.getBool(context, "value");
                                                        SimpleVeinminerClient.getConfig().setHudShowCount(showCount);
                                                        context.getSource().getPlayer().sendMessage(Text.of("Hud \"showCount\" set to " + (showCount ? "true" : "false")));
                                                        return 1;
                                                    })
                                            ).executes(context -> {
                                                boolean showCount = SimpleVeinminerClient.getConfig().hudDisplay.showCount;
                                                context.getSource().getPlayer().sendMessage(Text.of((showCount ? "Showing count" : "Not showing count")));
                                                return 1;
                                            })
                                    )
                            ).then(
                                    literal("spacing").then(
                                            argument("value", IntegerArgumentType.integer())
                                                    .executes((context) -> {
                                                        int spacing = IntegerArgumentType.getInteger(context, "value");
                                                        SimpleVeinminerClient.getConfig().setHudBlockNumberSpacing(spacing);
                                                        context.getSource().getPlayer().sendMessage(Text.of("Hud block number spacing set to " + spacing));
                                                        return 1;
                                                    })
                                    ).executes(context -> {
                                        int spacing = SimpleVeinminerClient.getConfig().hudDisplay.blockNumberSpacing;
                                        context.getSource().getPlayer().sendMessage(Text.of("Hud block number spacing is " + spacing));
                                        return 1;
                                    })
                            )
                    )
                    .then(
                            literal("highlight")
                                    .then(
                                            literal("updateRate").then(
                                                    argument("value", IntegerArgumentType.integer(0))
                                                            .executes((context) -> {
                                                                int updateRate = IntegerArgumentType.getInteger(context, "value");
                                                                SimpleVeinminerClient.getConfig().setHighlightUpdateRate(updateRate);
                                                                context.getSource().getPlayer().sendMessage(Text.of("Highlight update rate set to " + updateRate));
                                                                return 1;
                                                            })
                                            ).executes((context) -> {
                                                int updateRate = SimpleVeinminerClient.getConfig().highlight.updateRate;
                                                context.getSource().getPlayer().sendMessage(Text.of("Highlight update rate is " + updateRate));
                                                return 1;
                                            })
                                    ).then(
                                            literal("opacity")
                                                    .then(
                                                            argument("value", IntegerArgumentType.integer(1, 100))
                                                                    .executes((context) -> {
                                                                        int opacity = IntegerArgumentType.getInteger(context, "value");
                                                                        SimpleVeinminerClient.getConfig().setOpacity(opacity);
                                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight opacity set to " + opacity));
                                                                        return 1;
                                                                    })
                                                    ).executes((context) -> {
                                                        int opacity = SimpleVeinminerClient.getConfig().highlight.opacity;
                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight opacity is " + opacity));
                                                        return 1;
                                                    })
                                    ).then(
                                            literal("mode")
                                                    .then(
                                                            argument("value", HighlightModesArgumentType.highlightModes())
                                                                    .executes((context) -> {
                                                                        SimpleConfigClient.Highlight.MODES mode = HighlightModesArgumentType.getHighlightMode(context, "value");
                                                                        SimpleVeinminerClient.getConfig().setMode(mode);
                                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight mode set to " + mode.name().toUpperCase()));
                                                                        return 1;
                                                                    })
                                                    ).executes((context) -> {
                                                        SimpleConfigClient.Highlight.MODES mode = SimpleVeinminerClient.getConfig().highlight.mode;
                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight mode is " + mode.name()));
                                                        return 1;
                                                    })
                                    ).then(
                                            literal("highlightAllSides").then(
                                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                        boolean highlightAllSides = BoolArgumentType.getBool(context, "value");
                                                        SimpleVeinminerClient.getConfig().setHighlightAllSides(highlightAllSides);
                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight \"highlightAllSides\" set to " + (highlightAllSides ? "true" : "false")));
                                                        return 1;
                                                    })
                                            ).executes((context)->{
                                                boolean highlightAllSides = SimpleVeinminerClient.getConfig().highlight.highlightAllSides;
                                                context.getSource().getPlayer().sendMessage(Text.of((highlightAllSides ? "Highlighting all sides" : "Not highlighting connected sides")));
                                                return 1;
                                            })
                                    )/*.then(
                                            literal("onlyExposed").then(
                                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                        boolean onlyExposed = BoolArgumentType.getBool(context, "value");
                                                        SimpleVeinminerClient.getConfig().setOnlyExposed(onlyExposed);
                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight \"onlyExposed\" set to " + (onlyExposed ? "true" : "false")));
                                                        return 1;
                                                    })
                                            ).executes((context) -> {
                                                boolean onlyExposed = SimpleVeinminerClient.getConfig().highlight.onlyExposed;
                                                context.getSource().getPlayer().sendMessage(Text.of((onlyExposed ? "Highlighting only exposed blocks" : "Highlighting all blocks")));
                                                return 1;
                                            })
                                    )*/.then(
                                            literal("color").then(
                                                    argument("r", IntegerArgumentType.integer(0, 255)).then(
                                                            argument("g", IntegerArgumentType.integer(0, 255)).then(
                                                                    argument("b", IntegerArgumentType.integer(0, 255)).executes((context) -> {
                                                                        int r = IntegerArgumentType.getInteger(context, "r");
                                                                        int g = IntegerArgumentType.getInteger(context, "g");
                                                                        int b = IntegerArgumentType.getInteger(context, "b");
                                                                        SimpleVeinminerClient.getConfig().setColor(Color.ofRGB(r, g, b));
                                                                        context.getSource().getPlayer().sendMessage(Text.of("Highlight color set to " + r + ", " + g + ", " + b));
                                                                        return 1;
                                                                    })
                                                            )
                                                    )
                                            ).executes(context->{
                                                Color color = SimpleVeinminerClient.getConfig().highlight.color;
                                                context.getSource().getPlayer().sendMessage(Text.of("Highlight color is " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue()));
                                                return 1;
                                            })
                                    ).then(
                                            literal("doHighlight").then(
                                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                                        boolean outlineBlocks = BoolArgumentType.getBool(context, "value");
                                                        SimpleVeinminerClient.getConfig().setDoHighlight(outlineBlocks);
                                                        context.getSource().getPlayer().sendMessage(Text.of("Veinmining \"doHighlight\" set to " + (outlineBlocks ? "true" : "false")));
                                                        return 1;
                                                    })
                                            ).executes(context-> {
                                                boolean doHighlight = SimpleVeinminerClient.getConfig().highlight.doHighlight;
                                                context.getSource().getPlayer().sendMessage(Text.of((doHighlight ? "Highlighting blocks" : "Not highlighting blocks")));
                                                return 1;
                                            })
                                    )
                    ).then(
                            literal("keybindToggles").then(
                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                        boolean keybindToggles = BoolArgumentType.getBool(context, "value");
                                        SimpleVeinminerClient.getConfig().setKeybindToggles(keybindToggles);
                                        context.getSource().getPlayer().sendMessage(Text.of("Veinmining \"keybindToggles\" set to " + (keybindToggles ? "true" : "false")));
                                        return 1;
                                    })
                            ).executes(context-> {
                                boolean keybindToggles = SimpleVeinminerClient.getConfig().keybindToggles;
                                context.getSource().getPlayer().sendMessage(Text.of((keybindToggles ? "Keybind on toggle mode" : "Keybind on hold mode")));
                                return 1;
                            })
                    ).then(
                            literal("showMiningProgress").then(
                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                        boolean showMiningProgress = BoolArgumentType.getBool(context, "value");
                                        SimpleVeinminerClient.getConfig().setShowMiningProgress(showMiningProgress);
                                        context.getSource().getPlayer().sendMessage(Text.of("Veinmining \"showMiningProgress\" set to " + (showMiningProgress ? "true" : "false")));
                                        return 1;
                                    })
                            ).executes(context -> {
                                boolean showMiningProgress = SimpleVeinminerClient.getConfig().showMiningProgress;
                                context.getSource().getPlayer().sendMessage(Text.of((showMiningProgress ? "Showing mining progress" : "Not showing mining progress")));
                                return 1;
                            })
                    ).then(
                            literal("showRestrictionMessages").then(
                                    argument("value", BoolArgumentType.bool()).executes((context) -> {
                                        boolean showRestrictionMessages = BoolArgumentType.getBool(context, "value");
                                        SimpleVeinminerClient.getConfig().setShowRestrictionMessages(showRestrictionMessages);
                                        context.getSource().getPlayer().sendMessage(Text.of("Veinmining \"showRestrictionMessages\" set to " + (showRestrictionMessages ? "true" : "false")));
                                        return 1;
                                    })
                            ).executes(context -> {
                                boolean showRestrictionMessages = SimpleVeinminerClient.getConfig().showRestrictionMessages;
                                context.getSource().getPlayer().sendMessage(Text.of((showRestrictionMessages ? "Showing restriction messages" : "Not showing restriction messages")));
                                return 1;
                            })
                    ).then(
                            literal("reset").executes(context-> {
                                SimpleVeinminerClient.getConfig().resetClient();
                                context.getSource().getPlayer().sendMessage(Text.of("Resetting client config to default values"));
                                return 1;
                            })
                    )
            );
        });
    }
}
