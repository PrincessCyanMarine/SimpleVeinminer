package net.cyanmarine.simpleveinminer.client;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import net.cyanmarine.simpleveinminer.Constants;
import net.cyanmarine.simpleveinminer.SimpleVeinminer;
import net.cyanmarine.simpleveinminer.commands.CommandRegisterClient;
import net.cyanmarine.simpleveinminer.config.SimpleConfig;
import net.cyanmarine.simpleveinminer.config.SimpleConfigClient;
import net.cyanmarine.simpleveinminer.gui.ScreenBuilderType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class SimpleVeinminerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleVeinminer.MOD_ID + " - client");
    public static boolean isVeinMiningServerSide = false;
    public static AtomicBoolean isInstalledOnServerSide = new AtomicBoolean(false);
    static SimpleConfig.SimpleConfigCopy worldConfig;
    private static SimpleConfigClient config;
    public static KeyBinding veinMineKeybind = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding("key.simpleveinminer.veinminingKey", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.simpleveinminer.veinminerCategory", () -> config.keybindToggles));
    public static boolean veinMining;

    public static SimpleConfig.SimpleConfigCopy getWorldConfig() {
        if (worldConfig == null) return SimpleConfig.SimpleConfigCopy.from(config);
        return worldConfig;
    }

    public static SimpleConfigClient getConfig() {
        return config;
    }

    public static void drawBox(BufferBuilder buffer, Matrix4f positionMatrix, float red, float green, float blue, float alpha, Box box, @Nullable BlockPos pos, @Nullable List<BlockPos> blocks, boolean ignoreNeighbors) {
        float minX = (float) box.minX;
        float maxX = (float) box.maxX;
        float minY = (float) box.minY;
        float maxY = (float) box.maxY;
        float minZ = (float) box.minZ;
        float maxZ = (float) box.maxZ;

        boolean b = ignoreNeighbors || pos == null || blocks == null;

        if (b || minZ > 0 || !blocks.contains(pos.north())) {
            buffer.vertex(positionMatrix, minX, maxY, minZ).color(red, green, blue, alpha).texture(minX, minY).next();
            buffer.vertex(positionMatrix, minX, minY, minZ).color(red, green, blue, alpha).texture(minX, maxY).next();
            buffer.vertex(positionMatrix, maxX, minY, minZ).color(red, green, blue, alpha).texture(maxX, maxY).next();
            buffer.vertex(positionMatrix, maxX, maxY, minZ).color(red, green, blue, alpha).texture(maxX, minY).next();
        }

        if (b || maxZ < 1 || !blocks.contains(pos.south())) {
            buffer.vertex(positionMatrix, minX, maxY, maxZ).color(red, green, blue, alpha).texture(minX, minY).next();
            buffer.vertex(positionMatrix, minX, minY, maxZ).color(red, green, blue, alpha).texture(minX, maxY).next();
            buffer.vertex(positionMatrix, maxX, minY, maxZ).color(red, green, blue, alpha).texture(maxX, maxY).next();
            buffer.vertex(positionMatrix, maxX, maxY, maxZ).color(red, green, blue, alpha).texture(maxX, minY).next();
        }

        if (b || maxX < 1 || !blocks.contains(pos.east())) {
            buffer.vertex(positionMatrix, maxX, minY, maxZ).color(red, green, blue, alpha).texture(minY, maxZ).next();
            buffer.vertex(positionMatrix, maxX, minY, minZ).color(red, green, blue, alpha).texture(minY, minZ).next();
            buffer.vertex(positionMatrix, maxX, maxY, minZ).color(red, green, blue, alpha).texture(maxY, minZ).next();
            buffer.vertex(positionMatrix, maxX, maxY, maxZ).color(red, green, blue, alpha).texture(maxY, maxZ).next();
        }

        if (b || minX > 0 || !blocks.contains(pos.west())) {
            buffer.vertex(positionMatrix, minX, minY, maxZ).color(red, green, blue, alpha).texture(minY, maxZ).next();
            buffer.vertex(positionMatrix, minX, minY, minZ).color(red, green, blue, alpha).texture(minY, minZ).next();
            buffer.vertex(positionMatrix, minX, maxY, minZ).color(red, green, blue, alpha).texture(maxY, minZ).next();
            buffer.vertex(positionMatrix, minX, maxY, maxZ).color(red, green, blue, alpha).texture(maxY, maxZ).next();
        }

        if (b || maxY < 1 || !blocks.contains(pos.up())) {
            buffer.vertex(positionMatrix, maxX, maxY, minZ).color(red, green, blue, alpha).texture(maxX, minZ).next();
            buffer.vertex(positionMatrix, minX, maxY, minZ).color(red, green, blue, alpha).texture(minX, minZ).next();
            buffer.vertex(positionMatrix, minX, maxY, maxZ).color(red, green, blue, alpha).texture(minX, maxZ).next();
            buffer.vertex(positionMatrix, maxX, maxY, maxZ).color(red, green, blue, alpha).texture(maxX, maxZ).next();
        }

        if (b || minY > 0 || !blocks.contains(pos.down())) {
            buffer.vertex(positionMatrix, maxX, minY, minZ).color(red, green, blue, alpha).texture(maxX, minZ).next();
            buffer.vertex(positionMatrix, minX, minY, minZ).color(red, green, blue, alpha).texture(minX, minZ).next();
            buffer.vertex(positionMatrix, minX, minY, maxZ).color(red, green, blue, alpha).texture(minX, maxZ).next();
            buffer.vertex(positionMatrix, maxX, minY, maxZ).color(red, green, blue, alpha).texture(maxX, maxZ).next();
        }
    }

    @Override
    public void onInitializeClient() {
        veinMining = false;
        config = new SimpleConfigClient();
        config.load();
        worldConfig = SimpleConfig.SimpleConfigCopy.from(config);
        /*if (FabricLoader.getInstance().isModLoaded("cloth-config"))
            ConfigScreenBuilder.setMain(SimpleVeinminer.MOD_ID, ScreenBuilderType.CLOTH_CONFIG.create());
        else if (FabricLoader.getInstance().isModLoaded("yet-another-config-lib"))
            ConfigScreenBuilder.setMain(SimpleVeinminer.MOD_ID, ScreenBuilderType.YACL.create());*/
        //else
        //ConfigScreenBuilder.setMain(SimpleVeinminer.MOD_ID, new CoatScreenBuilder());

        new CommandRegisterClient();


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (veinMining != veinMineKeybind.isPressed()) {
                veinMining = !veinMining;
                if (config.keybindToggles)
                    client.player.sendMessage(veinMining ? Text.translatable("messages.simpleveinminer.veinminingToggled.on") : Text.translatable("messages.simpleveinminer.veinminingToggled.off"), true);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(veinMining);
                ClientPlayNetworking.send(Constants.NETWORKING_VEINMINE, buf);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            worldConfig = null;
            SimpleVeinminerClient.isInstalledOnServerSide.set(false);
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            worldConfig = null;
            SimpleVeinminerClient.isInstalledOnServerSide.set(false);
        });

        ClientPlayNetworking.registerGlobalReceiver(Constants.CONFIG_SYNC, (client, handler, buf, responseSender) -> {
            buf.retain();

            client.execute(() -> {
                worldConfig = SimpleConfig.copy(buf);
                SimpleVeinminerClient.isInstalledOnServerSide.set(true);
                buf.release();
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Constants.SERVERSIDE_UPDATE, (client, handler, buf, responseSender) -> {
            boolean newValue = buf.readBoolean();

            client.execute(() -> {
                isVeinMiningServerSide = newValue;
            });
        });

        LOGGER.info("Simple VeinMiner initialized");
    }
}
