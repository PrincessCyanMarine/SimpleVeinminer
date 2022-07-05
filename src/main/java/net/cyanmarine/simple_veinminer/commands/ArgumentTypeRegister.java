package net.cyanmarine.simple_veinminer.commands;

import net.cyanmarine.simple_veinminer.commands.argumentTypes.ShapeArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

import static net.cyanmarine.simple_veinminer.SimpleVeinminer.id;


public class ArgumentTypeRegister {
        public ArgumentTypeRegister() {
                ArgumentTypeRegistry.registerArgumentType(id("veinmining_shape"), ShapeArgumentType.class, ConstantArgumentSerializer.of(ShapeArgumentType::shape));
        }
}
