package net.cyanmarine.simpleveinminer.gui;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;

public enum ScreenBuilderType {
    CLOTH_CONFIG() {
        @Override
        public ConfigScreenBuilder<?> create() {
            return new ClothConfigScreenBuilder(() -> ConfigBuilder.create().setTransparentBackground(true).setShouldListSmoothScroll(true).setShouldTabsSmoothScroll(true));
        }
    };

    public abstract ConfigScreenBuilder<?> create();

}

