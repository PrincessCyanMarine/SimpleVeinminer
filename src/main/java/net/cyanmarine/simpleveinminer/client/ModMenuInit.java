package net.cyanmarine.simpleveinminer.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.cyanmarine.simpleveinminer.gui.ScreenBuilderType;

public class ModMenuInit implements ModMenuApi {
    private static final ClothConfigScreenBuilder configScreenBuilder = (ClothConfigScreenBuilder) ScreenBuilderType.CLOTH_CONFIG.create();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> configScreenBuilder.build(parent, SimpleVeinminer.getConfig());
    }
}
