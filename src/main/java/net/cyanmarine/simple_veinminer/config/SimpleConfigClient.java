package net.cyanmarine.simple_veinminer.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.math.Color;

@ConfigEntries(includeAll = true)
public class SimpleConfigClient extends SimpleConfig {
    @Transitive
    public Outline outline = new Outline();

    public boolean showRestrictionMessages = true;

    @ConfigEntry(comment = "Recommended: If you turn this on, turn restriction messages off and highlights on")
    @ConfigEntry.Boolean(falseKey = "keybindToggles.value.false", trueKey = "keybindToggles.value.true")
    public boolean keybindToggles = false;

    @ConfigEntries(includeAll = true)
    public static class Outline implements ConfigGroup {
        public boolean outlineBlocks = true;
        @ConfigEntry.Color(alphaMode = false)
        public Color outlineColor = Color.ofOpaque(0xFFFFFF);
    }
}
