package net.cyanmarine.simpleveinminer.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.math.Color;

@ConfigEntries(includeAll = true)
public class SimpleConfigClient extends SimpleConfig {
    public boolean showMiningProgress = true;
    public boolean showRestrictionMessages = true;

    @ConfigEntry(comment = "Recommended: If you turn this on, turn restriction messages off and highlights on")
    @ConfigEntry.Boolean(falseKey = "keybindToggles.value.false", trueKey = "keybindToggles.value.true")
    public boolean keybindToggles = false;

    @Transitive
    public Highlight highlight = new Highlight();

    public void resetClient() {
        this.showMiningProgress = true;
        this.showRestrictionMessages = true;
        this.keybindToggles = false;
        this.highlight = new Highlight();
        this.save();
    }

    public void setShowMiningProgress(boolean showMiningProgress) {
        this.showMiningProgress = showMiningProgress;
        this.save();
    }

    public void setShowRestrictionMessages(boolean showRestrictionMessages) {
        this.showRestrictionMessages = showRestrictionMessages;
        this.save();
    }

    public void setKeybindToggles(boolean keybindToggles) {
        this.keybindToggles = keybindToggles;
        this.save();
    }

    public void setDoHighlight(boolean doHighlight) {
        this.highlight.doHighlight = doHighlight;
        this.save();
    }

    public void setColor(Color color) {
        this.highlight.color = color;
        this.save();
    }

    public void setOpacity(int opacity) {
        this.highlight.opacity = opacity;
        this.save();
    }

    public void setMode(Highlight.MODES mode) {
        this.highlight.mode = mode;
        this.save();
    }

    public void setHighlightAllSides(boolean highlightAllSides) {
        this.highlight.highlightAllSides = highlightAllSides;
        this.save();
    }

    public void setOnlyExposed(boolean onlyExposed) {
        this.highlight.onlyExposed = onlyExposed;
        this.save();
    }

    @ConfigEntries(includeAll = true)
    public static class Highlight implements ConfigGroup {
        public boolean doHighlight = true;
        @ConfigEntry.Color(alphaMode = false)
        public Color color = Color.ofOpaque(0xFFFFFF);
        @ConfigEntry.Slider(valueKey = "opacity.value")
        @ConfigEntry.IntegerSliderInterval(1)
        @ConfigEntry.BoundedInteger(min = 0, max = 100)
        public int opacity = 39;
        public MODES mode = MODES.OUTLINE;
        public boolean highlightAllSides = false;
        public boolean onlyExposed = true;


        public enum MODES {
            OUTLINE,
            CUBE,
            CUBE_SHAPE,
            SHAPE
        }
    }
}
