package net.cyanmarine.simple_veinminer.integration;

import com.oroarmor.config.screen.ModMenuConfigScreen;
import net.cyanmarine.simple_veinminer.client.SimpleVeinminerClient;

public class ModMenuIntegration extends ModMenuConfigScreen {
    public ModMenuIntegration() {
        super(SimpleVeinminerClient.CONFIG);
    }
}
