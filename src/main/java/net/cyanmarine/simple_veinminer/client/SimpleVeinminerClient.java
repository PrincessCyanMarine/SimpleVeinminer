package net.cyanmarine.simple_veinminer.client;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.cyanmarine.simple_veinminer.Constants;
import net.cyanmarine.simple_veinminer.config.SimpleConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class SimpleVeinminerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner - Client");
    public static KeyBinding veinMineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.veinminer.veinmine", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.veinminer.veinminer"));
    private boolean veinMining;
    private static SimpleConfigClient config;

    @Override
    public void onInitializeClient() {
        veinMining = false;
        config = new SimpleConfigClient();
        config.load();
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) ConfigScreenBuilder.setMain("simple_veinminer", new ClothConfigScreenBuilder());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (veinMining != veinMineKeybind.isPressed()) {
                veinMining = !veinMining;
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(veinMining);
                ClientPlayNetworking.send(Constants.NETWORKING_VEINMINE, buf);
            }
        });

        LOGGER.info("Simple VeinMiner initialized");
    }

    public static SimpleConfigClient getConfig() {
        return config;
    }
}
