package net.cyanmarine.simpleveinminer.commands.argumenttypes;

import com.mojang.brigadier.context.CommandContext;
import net.cyanmarine.simpleveinminer.config.enums.*;
import net.minecraft.command.argument.EnumArgumentType;

public class AnchorArgumentType {
    public static VerticalAnchorArgumentType verticalAnchor() { return new VerticalAnchorArgumentType(); }
    public static <S> VerticalAnchor getVerticalAnchor(CommandContext<S> context, String id){
        return context.getArgument(id, VerticalAnchor.class);
    }
    public static HorizontalAnchorArgumentType horizontalAnchor() { return new HorizontalAnchorArgumentType(); }
    public static <S> HorizontalAnchor getHorizontalAnchor(CommandContext<S> context, String id){
        return context.getArgument(id, HorizontalAnchor.class);
    }

    public static class VerticalAnchorArgumentType extends EnumArgumentType<VerticalAnchor>
    {
        private VerticalAnchorArgumentType() {
            super(VerticalAnchor.CODEC, VerticalAnchor::values);
        }
    }
    public static class HorizontalAnchorArgumentType extends EnumArgumentType<HorizontalAnchor>
    {
        private HorizontalAnchorArgumentType() {
            super(HorizontalAnchor.CODEC, HorizontalAnchor::values);
        }
    }
}
