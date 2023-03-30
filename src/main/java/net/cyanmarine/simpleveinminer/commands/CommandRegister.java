package net.cyanmarine.simpleveinminer.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegister {
    public CommandRegister() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("veinmining")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(
                            literal("maxBlocks")
                                    .then(
                                            CommandManager.argument("value", IntegerArgumentType.integer(1, 10000))
                                                    .executes((context) -> {
                                                        int maxBlocks = IntegerArgumentType.getInteger(context, "value");
                                                        SimpleVeinminer.getConfig().limits.setMaxBlocks(maxBlocks);
                                                        context.getSource().sendMessage(Text.of("Veinmining \"max blocks\" set to " + maxBlocks));
                                                        return 1;
                                                    })
                                    )
                    ).then(
                            literal("")
                    )
                    .executes(context -> {
                        PlayerEntity player = context.getSource().getPlayer();
                        if (player != null)
                            player.sendMessage(Text.of("Options: maxBlocks"), false);
                        return 1;
                    })
            );
        });
    }
}
