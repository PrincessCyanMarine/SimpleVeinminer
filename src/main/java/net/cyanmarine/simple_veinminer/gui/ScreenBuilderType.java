package net.cyanmarine.simple_veinminer.gui;

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
    /*COAT() {
        @Override
        public ConfigScreenBuilder<?> create() {
            return new CoatScreenBuilder();
        }
    };*/

    public abstract ConfigScreenBuilder<?> create();

}

