package net.cyanmarine.simple_veinminer.config;

import com.oroarmor.config.BooleanConfigItem;
import com.oroarmor.config.screen.ConfigScreenBuilders;
import net.minecraft.text.TranslatableText;

public class SimpleBooleanConfigItem extends BooleanConfigItem {
    String tooltip;

    public SimpleBooleanConfigItem(String name, Boolean defaultValue, String details, String tooltip) {
        super(name, defaultValue, details);
        this.tooltip = tooltip;
    }

    static {
        ConfigScreenBuilders.register(SimpleBooleanConfigItem.class, (ConfigScreenBuilders.EntryBuilder<Boolean>) (configItem, entryBuilder, config) -> {
            String tooltip = "";
            if (configItem instanceof SimpleBooleanConfigItem) tooltip = ((SimpleBooleanConfigItem) configItem).tooltip;
            return entryBuilder.startBooleanToggle(new TranslatableText(configItem.getName()), configItem.getValue()).setDefaultValue(configItem::getDefaultValue).setSaveConsumer(configItem::setValue).setTooltip(new TranslatableText(tooltip)).build();
        });
    }
}
