package net.cyanmarine.simple_veinminer.commands.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.cyanmarine.simple_veinminer.Constants;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ShapeArgumentType implements ArgumentType<Constants.SHAPES> {
    private static final Collection<String> EXAMPLES = Arrays.asList("REGULAR", "HAMMER");
    private List<String> suggestions = Arrays.stream(Constants.SHAPES.values()).map((shape) -> shape.name().toLowerCase()).collect(Collectors.toList());

    public static final DynamicCommandExceptionType UNKNOWN_SHAPE_EXCEPTION = new DynamicCommandExceptionType((name) -> Text.translatable("messages.simple_veinminer.commands.shapes.unknown", name));

    @Override
    public Constants.SHAPES parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();

        if (!reader.canRead()) {
            reader.skip();
        }

        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String string = reader.getString().substring(argBeginning, reader.getCursor()).toUpperCase();
        try {
            return Constants.SHAPES.valueOf(string);
        } catch (IllegalArgumentException e) {
            //throw new SimpleCommandExceptionType(Text.literal("No shape with name" + string)).createWithContext(reader);

            throw UNKNOWN_SHAPE_EXCEPTION.createWithContext(reader, string);
        }
    }

    public static ShapeArgumentType shape() {
        return new ShapeArgumentType();
    }


    public static <S> Constants.SHAPES getShape(String name, CommandContext<S> context) {
        return context.getArgument(name, Constants.SHAPES.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(suggestions, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
