package net.cyanmarine.simpleveinminer.config.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum HorizontalAnchor implements StringIdentifiable {

    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    public static final Codec<HorizontalAnchor> CODEC;
    private final String id;

    HorizontalAnchor(String id) {
        this.id = id;
    }
    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(HorizontalAnchor::values);
    }
}
