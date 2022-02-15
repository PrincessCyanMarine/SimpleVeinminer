package net.cyanmarine.simple_veinminer.client;

import net.cyanmarine.simple_veinminer.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class SimpleVeinminerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SimpleVeinMiner - Client");
    private static KeyBinding veinMineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.veinminer.veinmine", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.veinminer.veinminer"));
    private boolean veinMining;

    @Override
    public void onInitializeClient() {
        veinMining = false;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (veinMining != veinMineKeybind.isPressed()) {
                veinMining = !veinMining;
                //client.player.sendMessage(new LiteralText(veinMining ? "true" : "false"), false);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(veinMining);
                ClientPlayNetworking.send(Constants.NETWORKING_VEINMINE.identifier, buf);
            }
        });

        LOGGER.info("Simple VeinMiner initialized");
    }
}
