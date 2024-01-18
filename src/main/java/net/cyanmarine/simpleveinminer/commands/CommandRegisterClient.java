package net.cyanmarine.simpleveinminer.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.shedaniel.math.Color;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.cyanmarine.simpleveinminer.client.SimpleVeinminerClient;
import net.cyanmarine.simpleveinminer.commands.argumenttypes.HighlightModesArgumentType;
import net.cyanmarine.simpleveinminer.config.SimpleConfigClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegisterClient {
    public CommandRegisterClient() {
        SimpleVeinminer.LOGGER.info("Initializing client commands");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("veinminingclient")
                    .then(
                            literal("highlight")
                                    .then(
                                            literal("opacity")
                                                    .then(
                                                            argument("value", IntegerArgumentType.integer(1, 10000))
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
                                    ).then(
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
                                    ).then(
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
