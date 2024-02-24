package net.cyanmarine.simpleveinminer.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.math.Color;

@ConfigEntries(includeAll = true)
public class SimpleConfigClient extends SimpleConfig {
    public int clientRadius = 1;
    public boolean showMiningProgress = true;
    public boolean showRestrictionMessages = true;

    @ConfigEntry(comment = "Recommended: If you turn this on, turn restriction messages off and highlights on")
    @ConfigEntry.Boolean(falseKey = "keybindToggles.value.false", trueKey = "keybindToggles.value.true")
    public boolean keybindToggles = false;
    @ConfigEntries.Exclude
    public boolean changed = false;

    @Transitive
    public Highlight highlight = new Highlight();

    @Transitive
    public HudDisplay hudDisplay = new HudDisplay();

    public void resetClient() {
        this.showMiningProgress = true;
        this.showRestrictionMessages = true;
        this.keybindToggles = false;
        this.highlight = new Highlight();
        this._save();
    }

    public void setClientRadius(int clientRadius) {
        this.clientRadius = Math.min(5, Math.max(1, clientRadius));
        this._save();
    }

    public void setShowMiningProgress(boolean showMiningProgress) {
        this.showMiningProgress = showMiningProgress;
        this._save();
    }

    public void setShowRestrictionMessages(boolean showRestrictionMessages) {
        this.showRestrictionMessages = showRestrictionMessages;
        this._save();
    }

    public void setKeybindToggles(boolean keybindToggles) {
        this.keybindToggles = keybindToggles;
        this._save();
    }

    public void setDoHighlight(boolean doHighlight) {
        this.highlight.doHighlight = doHighlight;
        this._save();
    }

    public void setColor(Color color) {
        this.highlight.color = color;
        this._save();
    }

    public void setOpacity(int opacity) {
        this.highlight.opacity = opacity;
        this._save();
    }

    public void setMode(Highlight.MODES mode) {
        this.highlight.mode = mode;
        this._save();
    }

    public void setHighlightAllSides(boolean highlightAllSides) {
        this.highlight.highlightAllSides = highlightAllSides;
        this._save();
    }

    /*public void setOnlyExposed(boolean onlyExposed) {
        this.highlight.onlyExposed = onlyExposed;
        this._save();
    }*/
    public void setHudShowCount(boolean showCount) {
        this.hudDisplay.showCount = showCount;
        this._save();
    }
    public void setHudShowBlock(boolean showBlock) {
        this.hudDisplay.showBlock = showBlock;
        this._save();
    }
    public void setHudX(int x) {
        this.hudDisplay.x = x;
        this._save();
    }
    public void setHudY(int y) {
        this.hudDisplay.y = y;
        this._save();
    }
    public void setHudVerticalAnchor(HudDisplay.VERTICAL_ANCHOR vertical_anchor) {
        this.hudDisplay.vertical_anchor = vertical_anchor;
        this._save();
    }
    public void setHudHorizontalAnchor(HudDisplay.HORIZONTAL_ANCHOR horizontal_anchor) {
        this.hudDisplay.horizontal_anchor = horizontal_anchor;
        this._save();
    }
    public void setHudBlockNumberSpacing(int blockNumberSpacing) {
        this.hudDisplay.blockNumberSpacing = blockNumberSpacing;
        this._save();
    }

    public final void _save() {
        this.save();
        this.changed = true;
    }
    public final boolean isChanged() {
        if (this.changed) {
            this.changed = false;
            return true;
        }
        return false;
    }

    public void setHighlightUpdateRate(int updateRate) {
        this.highlight.updateRate = updateRate;
        this._save();
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
        // public boolean onlyExposed = false;
        public int updateRate = 20;


        public enum MODES {
            OUTLINE,
            CUBE,
            CUBE_SHAPE,
            SHAPE
        }
    }

    @ConfigEntries(includeAll = true)
    public static class HudDisplay implements ConfigGroup {
        public boolean showCount = true;
        public VERTICAL_ANCHOR vertical_anchor = VERTICAL_ANCHOR.CENTER;
        public HORIZONTAL_ANCHOR horizontal_anchor = HORIZONTAL_ANCHOR.CENTER;
        public int x = 16;
        public int y = 0;
        public boolean showBlock = true;
        public int blockNumberSpacing = 16;

        public enum VERTICAL_ANCHOR {
            TOP,
            CENTER,
            BOTTOM
        }
        public enum HORIZONTAL_ANCHOR {
            LEFT,
            CENTER,
            RIGHT
        }
    }
}
