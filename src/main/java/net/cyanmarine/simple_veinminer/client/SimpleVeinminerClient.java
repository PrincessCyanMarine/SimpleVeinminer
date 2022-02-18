package net.cyanmarine.simple_veinminer.client;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.cyanmarine.simple_veinminer.Constants;
import net.cyanmarine.simple_veinminer.config.SimpleConfig;
import net.cyanmarine.simple_veinminer.config.SimpleConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class SimpleVeinminerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner - Client");
    private static SimpleConfigClient config;
    public static KeyBinding veinMineKeybind = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding("key.simple_veinminer.veinminingKey", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.simple_veinminer.veinminerCategory", () -> config.keybindToggles));
    public boolean veinMining;
    static SimpleConfig.SimpleConfigCopy worldConfig;

    public static SimpleConfig.SimpleConfigCopy getWorldConfig() {
        if (worldConfig == null) return SimpleConfig.SimpleConfigCopy.from(config);
        return worldConfig;
    }

    @Override
    public void onInitializeClient() {
        veinMining = false;
        config = new SimpleConfigClient();
        config.load();
        worldConfig = SimpleConfig.SimpleConfigCopy.from(config);
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) ConfigScreenBuilder.setMain("simple_veinminer", new ClothConfigScreenBuilder(() -> ConfigBuilder.create().setTransparentBackground(true).setShouldListSmoothScroll(true).setShouldTabsSmoothScroll(true)));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (veinMining != veinMineKeybind.isPressed()) {
                veinMining = !veinMining;
                if (config.keybindToggles) client.player.sendMessage(veinMining ? new TranslatableText("messages.simple_veinminer.veinminingToggled.on") : new TranslatableText("messages.simple_veinminer.veinminingToggled.off"), true);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(veinMining);
                ClientPlayNetworking.send(Constants.NETWORKING_VEINMINE, buf);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> { worldConfig = null; });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> { veinMining = false; });

        ClientPlayNetworking.registerGlobalReceiver(Constants.CONFIG_SYNC, (client, handler, buf, responseSender) -> {
            buf.retain();

            client.execute(() -> {
                worldConfig = SimpleConfig.copy(buf);
                buf.release();
            });
        });

        LOGGER.info("Simple VeinMiner initialized");
    }

    public static SimpleConfigClient getConfig() {
        return config;
    }
}
