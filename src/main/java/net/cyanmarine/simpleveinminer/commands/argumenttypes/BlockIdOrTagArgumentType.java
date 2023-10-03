package net.cyanmarine.simpleveinminer.commands.argumenttypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockIdOrTagArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "#stone", "#minecraft:stone");
    private final RegistryWrapper<Block> registryWrapper;

    public BlockIdOrTagArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.createWrapper(RegistryKeys.BLOCK);
    }

    public static String parse(RegistryWrapper<Block> registryWrapper, StringReader reader) throws CommandSyntaxException {
        String res = BlockArgumentParser.blockOrTag(registryWrapper, reader, false).map((result) ->
                        Registries.BLOCK.getKey(result.blockState().getBlock()).map(blockRegistryKey -> blockRegistryKey.getValue().toString()).orElse(null),
                (result) ->
                        result.tag().getTagKey().map(blockTagKey -> "#" + blockTagKey.id().toString()).orElse(null)
        );
        if (res == null) throw new SimpleCommandExceptionType(Text.of("Invalid block")).createWithContext(reader);
        return res;
    }

    public static String getBlockIdOrTag(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    public static BlockIdOrTagArgumentType blockIdOrTag(CommandRegistryAccess commandRegistryAccess) {
        return new BlockIdOrTagArgumentType(commandRegistryAccess);
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        return parse(this.registryWrapper, stringReader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return BlockArgumentParser.getSuggestions(this.registryWrapper, builder, true, false);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
