package net.cyanmarine.simple_veinminer.config;

import com.oroarmor.config.BooleanConfigItem;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItemGroup;
import me.shedaniel.math.Color;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

public class SimpleConfigClient extends Config {
    public static final List<ConfigItemGroup> configs = List.of(new ClientGroup());

    public SimpleConfigClient() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "simpleveinminer_client.json"), "simple_veinminer");
    }

    public static class ClientGroup extends ConfigItemGroup {
        public static final BooleanConfigItem outlineVein = new BooleanConfigItem("outline_vein", true, "Outline blocks that will be veinmined");
        public static final ColorConfigItem outlineColor = new ColorConfigItem("outline_color", Color.ofOpaque(0xFFFFFF), "veinmine.config.outline_color");

        public ClientGroup() {
            super(List.of(outlineVein, outlineColor), "client");
        }
    }
}
