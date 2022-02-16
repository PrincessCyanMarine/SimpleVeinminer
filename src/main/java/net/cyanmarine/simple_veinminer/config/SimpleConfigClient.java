package net.cyanmarine.simple_veinminer.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.math.Color;

public class SimpleConfigClient extends SimpleConfig {
    @Transitive
    public Outline outline = new Outline();

    @ConfigEntries
    public static class Outline implements ConfigGroup {
        @ConfigEntry(comment = "messages.outline_may_differ", tooltipTranslationKeys = "messages.outline_may_differ")
        public boolean outlineBlocks = true;
        @ConfigEntry.Color(alphaMode = false)
        public Color outlineColor = Color.ofTransparent(0xFFFFFF);
    }
}
