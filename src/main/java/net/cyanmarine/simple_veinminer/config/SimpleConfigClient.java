package net.cyanmarine.simple_veinminer.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.math.Color;

@ConfigEntries
public class SimpleConfigClient extends SimpleConfig {
    @Transitive
    public Outline outline = new Outline();

    public boolean showRestrictionMessages = true;

    @ConfigEntry(tooltipTranslationKeys = {"tooltip.toggleModeRecommendation.1", "tooltip.toggleModeRecommendation.2"}, comment = "Recommended: If you turn this on, turn restriction messages off and highlights on")
    @ConfigEntry.Boolean(falseTranslationKey = "keybindToggles.value.false", trueTranslationKey = "keybindToggles.value.true")
    public boolean keybindToggles = false;

    @ConfigEntries
    public static class Outline implements ConfigGroup {
        public boolean outlineBlocks = true;
        @ConfigEntry.Color(alphaMode = false)
        public Color outlineColor = Color.ofTransparent(0xFFFFFF);
    }
}
