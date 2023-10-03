package net.cyanmarine.simpleveinminer.gui;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
//import me.lortseam.completeconfig.gui.yacl.YaclScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;

public enum ScreenBuilderType {
    CLOTH_CONFIG() {
        @Override
        public ConfigScreenBuilder<?> create() {
            return new ClothConfigScreenBuilder(() -> ConfigBuilder.create().setTransparentBackground(true).setShouldListSmoothScroll(true).setShouldTabsSmoothScroll(true));
        }
    };

    /*YACL() {
        @Override
        public ConfigScreenBuilder<?> create() {
            return new YaclScreenBuilder();
        }
    };*/

    public abstract ConfigScreenBuilder<?> create();

}

