package net.cyanmarine.simpleveinminer.commands.argumenttypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.cyanmarine.simpleveinminer.config.SimpleConfigClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class HighlightModesArgumentType implements ArgumentType<SimpleConfigClient.Highlight.MODES> {

    public static HighlightModesArgumentType highlightModes() {
        return new HighlightModesArgumentType();
    }

    public static <T extends CommandSource> SimpleConfigClient.Highlight.MODES getHighlightMode(CommandContext<T> context, java.lang.String name) throws CommandSyntaxException {
        return context.getArgument(name, SimpleConfigClient.Highlight.MODES.class);
    }

    @Override
    public SimpleConfigClient.Highlight.MODES parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead())
            reader.skip();

        while (reader.canRead() && reader.peek() != ' ') reader.skip();

        String item = reader.getString().substring(argBeginning, reader.getCursor());
        try {
            return SimpleConfigClient.Highlight.MODES.valueOf(item.toUpperCase());
        } catch (Exception e) {
            throw new SimpleCommandExceptionType(Text.of("Invalid mode: " + item)).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (SimpleConfigClient.Highlight.MODES mode : SimpleConfigClient.Highlight.MODES.values()) {
            if (mode.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                builder.suggest(mode.name());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
