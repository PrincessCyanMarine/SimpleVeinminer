package net.cyanmarine.simpleveinminer.commands.argumenttypes;

import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class ArgumentTypes {
    public ArgumentTypes() {
        SimpleVeinminer.LOGGER.info("Initializing command argument types");
        ArgumentTypeRegistry.registerArgumentType(SimpleVeinminer.getId("list_item"), RestrictionListArgumentType.class, ConstantArgumentSerializer.of(() -> RestrictionListArgumentType.listItem()));
        ArgumentTypeRegistry.registerArgumentType(SimpleVeinminer.getId("tag_list"), TagListArgumentType.class, ConstantArgumentSerializer.of(() -> TagListArgumentType.listItem()));
        ArgumentTypeRegistry.registerArgumentType(SimpleVeinminer.getId("block_id_or_tag"), BlockIdOrTagArgumentType.class, ConstantArgumentSerializer.of(BlockIdOrTagArgumentType::blockIdOrTag));
        ArgumentTypeRegistry.registerArgumentType(SimpleVeinminer.getId("highlight_modes"), HighlightModesArgumentType.class, ConstantArgumentSerializer.of(HighlightModesArgumentType::highlightModes));
    }
}
