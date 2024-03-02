package net.cyanmarine.simpleveinminer.config.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum VerticalAnchor implements StringIdentifiable {
    TOP("top"),
    CENTER("center"),
    BOTTOM("bottom");

    public static final Codec<VerticalAnchor> CODEC;
    private final String id;

    VerticalAnchor(String id) {
        this.id = id;
    }
    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(VerticalAnchor::values);
    }
}
