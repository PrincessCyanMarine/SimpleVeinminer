package net.cyanmarine.simpleveinminer.config.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum SnowflakeAnchor implements StringIdentifiable {
    TOP("top"),
    RIGHT("right"),
    BOTTOM("bottom"),
    MIXED_TOP ("mixed_top"),
    MIXED_BOTTOM ("mixed_bottom");

    public static final Codec<SnowflakeAnchor> CODEC;
    private final String id;

    SnowflakeAnchor(String id) {
        this.id = id;
    }
    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(SnowflakeAnchor::values);
    }
}
