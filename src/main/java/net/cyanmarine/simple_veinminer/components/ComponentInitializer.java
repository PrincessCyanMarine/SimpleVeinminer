package net.cyanmarine.simple_veinminer.components;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.cyanmarine.simple_veinminer.SimpleVeinminer;

public class ComponentInitializer implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SimpleVeinminer.VEINMINING_SHAPE, player -> new VeinminingShapeComponent(player), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
