package net.cyanmarine.simpleveinminer.commands.argumenttypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TagListArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "#stone", "#minecraft:stone");

    public TagListArgumentType() {
    }

    public static String getListMember(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, String.class);
    }

    public static TagListArgumentType listItem() {
        return new TagListArgumentType();
    }

    public String parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead())
            reader.skip();

        while (reader.canRead() && reader.peek() != ' ') reader.skip();

        String item = reader.getString().substring(argBeginning, reader.getCursor());
        if (SimpleVeinminer.getConfig().restrictions.restrictionTags.tags.contains("minecraft:" + item))
            return "minecraft:" + item;
        if (SimpleVeinminer.getConfig().restrictions.restrictionTags.tags.contains(item)) return item;
        throw new SimpleCommandExceptionType(Text.of("Invalid block: " + item)).createWithContext(reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String item : SimpleVeinminer.getConfig().restrictions.restrictionTags.tags)
            if (item.toLowerCase().startsWith(builder.getRemainingLowerCase()) || item.toLowerCase().startsWith("minecraft:" + builder.getRemainingLowerCase()))
                builder.suggest(item);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
