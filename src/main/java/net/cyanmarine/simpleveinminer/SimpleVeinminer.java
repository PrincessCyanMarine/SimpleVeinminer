package net.cyanmarine.simpleveinminer;

import net.cyanmarine.simpleveinminer.client.SimpleVeinminerClient;
import net.cyanmarine.simpleveinminer.commands.CommandRegister;
import net.cyanmarine.simpleveinminer.commands.argumenttypes.ArgumentTypes;
import net.cyanmarine.simpleveinminer.config.SimpleConfig;
import net.cyanmarine.simpleveinminer.server.SimpleVeinminerServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SimpleVeinminer implements ModInitializer {
    public static final String MOD_ID = "simpleveinminer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GameRules.Key<GameRules.BooleanRule> SERVER_SIDE_VEINMINING = GameRuleRegistry.register("doServerSideVeinmining", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false, (server, rule) -> {
        Collection<ServerPlayerEntity> players = PlayerLookup.all(server);

        boolean newValue = rule.get();

        for (ServerPlayerEntity player : players) {
            if (newValue)
                player.sendMessage(Text.of("Server side veinmining was turned on. Mining while shifting will veinmine"));
            else
                player.sendMessage(Text.of("Server side veinmining was turned off"));

            sendServerSideVeinminingUpdate(player, newValue);
        }
    }));
    static MinecraftServer server;
    private static ArrayList<UUID> playersVeinMining;

    public static Identifier getId(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static ArrayList<BlockPos> getBlocksToVeinmine(BlockPos pos, BlockState state, int maxBlocks, PlayerEntity player) {
        World world = player.getWorld();

        Block compareTo = state.getBlock();

        ArrayList<BlockPos> blocksToBreak = new ArrayList<>();

        int i, x, y, z;

        blocksToBreak.add(pos);
        for (i = 0; i < maxBlocks && i < blocksToBreak.size(); i++) {
            BlockPos currentPos = blocksToBreak.get(i);
            for (x = -1; x <= 1; x++)
                for (y = -1; y <= 1; y++)
                    for (z = -1; z <= 1; z++) {
                        if (blocksToBreak.size() >= maxBlocks) return blocksToBreak;
                        BlockPos testPos = currentPos.add(x, y, z);
                        if (blocksToBreak.size() < maxBlocks && !blocksToBreak.contains(testPos) && world.getBlockState(testPos).getBlock().equals(compareTo))
                            blocksToBreak.add(testPos);
                    }
        }

        return blocksToBreak;
    }

    public static BlockHitResult getBlockHitResult(PlayerEntity player) {
        // Not stolen from Magna cough cough
        Vec3d cameraPos = player.getCameraPosVec(1);
        Vec3d rotation = player.getRotationVec(1);
        double reachDistance = player.isCreative() ? 5.0F : 4.5F;
        Vec3d combined = cameraPos.add(rotation.x * reachDistance, rotation.y * reachDistance, rotation.z * reachDistance);

        return player.getWorld().raycast(new RaycastContext(cameraPos, combined, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }

    public static int getMaxBlocks(Item item) {
        SimpleConfig config = SimpleVeinminer.getConfig();

        int maxBlocks = config.limits.maxBlocks;
        if (config.limits.materialBasedLimits) {
            int limitingFactor = 6;

            if (item instanceof ToolItem)
                limitingFactor -= ((ToolItem) item).getMaterial().getMiningLevel() + 1;
            else if (item instanceof ShearsItem)
                limitingFactor -= ToolMaterials.IRON.getMiningLevel() + 1;

            maxBlocks = maxBlocks / Math.max(1, limitingFactor);
        }

        return maxBlocks;
    }

    private static boolean listIncludes(Block block, SimpleConfig.Restrictions restrictions) {
        for (int i = 0; i < restrictions.restrictionList.list.size(); i++) {
            String name = restrictions.restrictionList.list.get(i);
            Identifier identifier = new Identifier(name.replaceAll("[^a-z0-9-_:]", ""));
            if (name.startsWith("#")) {
                if (block.getDefaultState().isIn(TagKey.of(Registries.BLOCK.getKey(), identifier))) return true;
            } else {
                Block compareTo = Registries.BLOCK.get(identifier);
                if (compareTo.equals(block)) return true;
            }
        }
        return false;
    }

    public static boolean canVeinmine(PlayerEntity player, BlockState state, SimpleConfig.Restrictions restrictions) {
        if (restrictions.creativeBypass && player.isCreative()) return true;

        Item hand = player.getMainHandStack().getItem();
        boolean sendMessage = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && SimpleVeinminerClient.getConfig().showRestrictionMessages;


        if (restrictions.restrictionList.listType != SimpleConfig.Restrictions.RestrictionList.ListType.NONE) {
            boolean isInList = listIncludes(state.getBlock(), restrictions);
            if ((restrictions.restrictionList.listType == SimpleConfig.Restrictions.RestrictionList.ListType.BLACKLIST) == isInList)
                return false;
        }

        if (!restrictions.canVeinmineWithEmptyHand && !(hand instanceof ToolItem || hand instanceof ShearsItem)) {
            if (sendMessage) player.sendMessage(Text.translatable("messages.simpleveinminer.restriction.tool"), true);
            return false;
        }

        if (restrictions.canOnlyUseSuitableTools && !player.getMainHandStack().isSuitableFor(state)) {
            if (sendMessage)
                player.sendMessage(Text.translatable("messages.simpleveinminer.restriction.specificTool"), true);
            return false;
        }

        if (!restrictions.canVeinmineHungry && player.getHungerManager().getFoodLevel() < 1) {
            if (sendMessage) player.sendMessage(Text.translatable("messages.simpleveinminer.restriction.hungry"), true);
            return false;
        }

        if (sendMessage) player.sendMessage(Text.of(""), true);

        return true;
    }

    public static void sendServerSideVeinminingUpdate(ServerPlayerEntity player, boolean value) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(value);
        ServerPlayNetworking.send(player, Constants.SERVERSIDE_UPDATE, buf);
    }

    public static void syncConfig(ServerPlayerEntity player) {
        SimpleConfig config = getConfig();
        PacketByteBuf buf = config.WritePacket();
        ServerPlayNetworking.send(player, Constants.CONFIG_SYNC, buf);
    }

    public static void SyncConfigForAllPlayers() {
        if (server == null) return;
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            syncConfig(player);
        }
    }

    public static boolean isVeinmining(PlayerEntity player) {
        return (player.isSneaking() && player.getWorld().getGameRules().getBoolean(SERVER_SIDE_VEINMINING)) || playersVeinMining.contains(player.getUuid());
    }

    private static void setVeinmining(PlayerEntity player, boolean isVeinMining) {
        if (isVeinMining)
            playersVeinMining.add(player.getUuid());
        else
            playersVeinMining.remove(player.getUuid());
    }

    public static SimpleConfig getConfig() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return SimpleVeinminerClient.getConfig();
        else
            return SimpleVeinminerServer.getConfig();
    }

    public static BlockPos[] getNeighbors(BlockPos pos) {
        return new BlockPos[]{
                pos.up(), pos.down(), pos.west(), pos.east(), pos.north(), pos.south()
        };
    }

    @Override
    public void onInitialize() {
        // CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> new ConfigCommand<ServerCommandSource>(CONFIG).register(dispatcher, p -> p.hasPermissionLevel(2)));
        playersVeinMining = new ArrayList<>();
        ServerPlayNetworking.registerGlobalReceiver(Constants.NETWORKING_VEINMINE, (server, player, handler, buf, sender) -> {
            boolean isVeinMining = buf.readBoolean();
            LOGGER.error("update: {}", isVeinMining);
            server.execute(() -> setVeinmining(player, isVeinMining));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            boolean serverSideVeinmining = server.getGameRules().getBoolean(SERVER_SIDE_VEINMINING);
            ServerPlayerEntity player = handler.getPlayer();

            sendServerSideVeinminingUpdate(player, serverSideVeinmining);

            if (serverSideVeinmining)
                player.sendMessage(Text.translatable("This server has server side veinmining turned on. Mining while shifting will veinmine"));

            syncConfig(player);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server1) -> setVeinmining(handler.getPlayer(), false));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SimpleVeinminer.server = server;
            LOGGER.info("New server");
        });

        new ArgumentTypes();
        new CommandRegister();

        LOGGER.info("Simple VeinMiner initialized");
    }
}
