package net.cyanmarine.simple_veinminer.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.cyanmarine.simple_veinminer.Constants;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.cyanmarine.simple_veinminer.commands.argumentTypes.ShapeArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.Locale;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegister {
    public CommandRegister() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("veinmining")
                    .requires(source -> source.isExecutedByPlayer())
                    .then(
                        literal("shape")
                            .then(
                                CommandManager.argument("shapeArgument", ShapeArgumentType.shape())
                                    .executes((context)-> {
                                        Constants.SHAPES shape = ShapeArgumentType.getShape("shapeArgument", context);
                                        PlayerEntity player = context.getSource().getPlayer();
                                        if (player != null) {
                                            SimpleVeinminer.LOGGER.info(player.getDisplayName().getString());
                                            player.sendMessage(Text.of("Veinmining shape set to " + shape));
                                            SimpleVeinminer.VEINMINING_SHAPE.get(player).setShape(shape);
                                        }
                                        return 1;
                                    })
                            ).executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();
                                if (player != null) {
                                    player.sendMessage(Text.of("Shape: " + SimpleVeinminer.VEINMINING_SHAPE.get(player).getShape()));
                                }
                                return 1;
                            })
                    )
                    .then(
                        literal("radius")
                            .then(
                                CommandManager.argument("radius", IntegerArgumentType.integer(0, 10))
                                        .executes((context)-> {
                                            int radius =  IntegerArgumentType.getInteger(context, "radius");
                                            PlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                SimpleVeinminer.LOGGER.info(player.getDisplayName().getString());
                                                player.sendMessage(Text.of("Veinmining radius set to " + radius));
                                                SimpleVeinminer.VEINMINING_SHAPE.get(player).setRadius(radius);
                                            }
                                            return 1;
                                        })
                            ).executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();
                                if (player != null) {
                                    player.sendMessage(Text.of("Radius: " + SimpleVeinminer.VEINMINING_SHAPE.get(player).getRadius()));
                                }
                                return 1;
                            })
                    )
                    .then(
                            literal("shapes").executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();
                                String s = "";
                                Constants.SHAPES[] shapes = Constants.SHAPES.values();
                                for (int i = 0; i < shapes.length; i++) {
                                    s += shapes[i].name().toLowerCase();
                                    if (i < shapes.length - 1)
                                        s += ", ";
                                }
                                player.sendMessage(Text.of(s));
                                return 1;
                            })
                    )
                    .executes(context -> {
                        PlayerEntity player = context.getSource().getPlayer();
                        if (player != null)
                            player.sendMessage(Text.of("Options: shape, shapes, radius"));
                        return 1;
                    })
            );
        });
    }
}
