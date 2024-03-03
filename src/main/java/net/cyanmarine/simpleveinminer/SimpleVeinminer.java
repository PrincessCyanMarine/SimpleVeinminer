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
import net.minecraft.block.Blocks;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

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
    private static HashMap<UUID, Integer> playersRadius;

    public static Identifier getId(String name) {
        return new Identifier(MOD_ID, name);
    }

    private static final BlockState[] STATES = {
            Blocks.RED_STAINED_GLASS.getDefaultState(),
            Blocks.ORANGE_STAINED_GLASS.getDefaultState(),
            Blocks.YELLOW_STAINED_GLASS.getDefaultState(),
            Blocks.LIME_STAINED_GLASS.getDefaultState(),
            Blocks.GREEN_STAINED_GLASS.getDefaultState(),
            Blocks.CYAN_STAINED_GLASS.getDefaultState(),
            Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState(),
            Blocks.BLUE_STAINED_GLASS.getDefaultState(),
            Blocks.PURPLE_STAINED_GLASS.getDefaultState(),
            Blocks.MAGENTA_STAINED_GLASS.getDefaultState(),
            Blocks.PINK_STAINED_GLASS.getDefaultState(),
            Blocks.BROWN_STAINED_GLASS.getDefaultState(),
            Blocks.BLACK_STAINED_GLASS.getDefaultState(),
            Blocks.GRAY_STAINED_GLASS.getDefaultState(),
            Blocks.LIGHT_GRAY_STAINED_GLASS.getDefaultState(),
            Blocks.WHITE_STAINED_GLASS.getDefaultState()
    };


    private static ArrayList<BlockPos> testAroundBlock(@Nullable ArrayList<BlockPos> blocksTested, ArrayList<BlockPos> blocksToBreak, int maxBlocks, World world, BlockState compareTo, int radius, BlockPos currentPos, int depth, boolean debug, @Nullable List<String> tagList, boolean chainReactions, @Nullable PlayerEntity player, SimpleConfig.Restrictions restrictions) {
        if (blocksTested == null) blocksTested = new ArrayList<>();
        ArrayList<BlockPos> blocksToCrawl = new ArrayList<>();
        int x, y, z;
        for (x = -1; x <= 1; x++)
            for (y = -1; y <= 1; y++)
                for (z = -1; z <= 1; z++) {
                    if (blocksToBreak.size() >= maxBlocks) return blocksToCrawl;
                    BlockPos testPos = currentPos.add(x, y, z);
                    if (blocksTested.contains(testPos)) continue;
                    blocksTested.add(testPos);
                    //LOGGER.info("Blocks tested: " + blocksTested.size());
                    //LOGGER.info("Blocks tested: " + blocksTested.size());

                    if (blocksToBreak.size() < maxBlocks && !blocksToBreak.contains(testPos)) {
                        BlockState testState = world.getBlockState(testPos);
                        if (isSameBlock(testState, compareTo, tagList, player, restrictions)) {
                            if ((testState.isOf(Blocks.TALL_GRASS) || testState.isOf(Blocks.TALL_SEAGRASS) || testState.isOf(Blocks.LARGE_FERN)) && (blocksToBreak.contains(testPos.up()) || blocksToBreak.contains(testPos.down()))) continue;
                            if (chainReactions) {
                                boolean skip = false;
                                if (testState.isIn(TagKey.of(Registries.BLOCK.getKey(), SimpleVeinminer.getId("break_bottom_most")))) {
                                    while (isSameBlock(world.getBlockState(testPos.down()), testState, null,  player, restrictions)) {
                                        if (blocksToBreak.contains(testPos.down())) {
                                            skip = true;
                                            break;
                                        }
                                        testPos = testPos.down();
                                    }
                                    if (skip) continue;
                                }
                                if (testState.isIn(TagKey.of(Registries.BLOCK.getKey(), SimpleVeinminer.getId("break_top_most")))) {
                                    while (isSameBlock(world.getBlockState(testPos.up()), testState, null, null, null)) {
                                        if (blocksToBreak.contains(testPos.up())) {
                                            skip = true;
                                            break;
                                        }
                                        testPos = testPos.up();
                                    }
                                    if (skip) continue;
                                }
                            }
                            blocksToBreak.add(testPos);
                        }
                        else {
                            if (debug) {
                                world.setBlockState(testPos, STATES[depth]);
                            }
                            if (!blocksToCrawl.contains(testPos)) {
                                // LOGGER.info("Adding block to crawl. x: " + testPos.getX() + " y: " + testPos.getY() + " z: " + testPos.getZ() + " depth: " + depth);
                                blocksToCrawl.add(testPos);
                            }
                        }
                    }
                }
        if (radius > 1 && depth == 0) {
            ArrayList<ArrayList<ArrayList<BlockPos>>> crawlers = new ArrayList<>();
            crawlers.add(new ArrayList<>());
            crawlers.get(0).add(new ArrayList<>(blocksToCrawl));
            for (int d = 0; d < crawlers.size() && d < radius - 1; d++) {
                int t;
                t = 0;
                // LOGGER.info("Crawling through depth " + d);
                ArrayList<ArrayList<BlockPos>> _crawlers = crawlers.get(d);
                ArrayList<ArrayList<BlockPos>> next = new ArrayList<>();
                for (ArrayList<BlockPos> crawler : _crawlers) {
                    for (BlockPos pos : crawler) {
                        t++;
                        ArrayList<BlockPos> newCrawler = (testAroundBlock(blocksTested, blocksToBreak, maxBlocks, world, compareTo, radius, pos, 1 + d, debug, tagList, chainReactions, player, restrictions));

                        if (d < radius - 2 && !newCrawler.isEmpty()) {
                            ///LOGGER.info("New crawler size (pre filter): " + newCrawler.size());
                            ArrayList<BlockPos> filteredCrawler = new ArrayList<>();
                            for (BlockPos newCrawlerPos : newCrawler) {
                                if (blocksToCrawl.contains(newCrawlerPos)) continue;
                                filteredCrawler.add(newCrawlerPos);
                                blocksToCrawl.add(newCrawlerPos);
                            }
                            if (!filteredCrawler.isEmpty()) next.add(filteredCrawler);
                            //LOGGER.info("New crawler size (post filter): " + newCrawler.size());
                        }
                    }
                }
                //LOGGER.info("Crawled through " + t + " blocks on pass #" + (d + 1));
                //LOGGER.info("Added " + next.size() + " blocks to the next pass");
                if (!next.isEmpty()) crawlers.add(next);
                //LOGGER.info("Crawlers size: " + crawlers.size());
                //LOGGER.info("Finished crawling " + d);
            }
        }
        return blocksToCrawl;
    }

    public static ArrayList<BlockPos> getBlocksToVeinmine(BlockPos pos, BlockState state, int maxBlocks, int radius, SimpleConfig.Limits.SPREAD_ACCURACY spreadAccuracy, PlayerEntity player, boolean debug, boolean chainReactions) {
        World world = player.getWorld();

        ArrayList<BlockPos> blocksToBreak = new ArrayList<>();
        ArrayList<BlockPos> blocksTested = new ArrayList<>();
        boolean isDebug = debug && !world.isClient();

        SimpleConfig.Restrictions.RestrictionTags rTags = getConfig().restrictions.restrictionTags;
        List<String> tagList = null;
        if (rTags.enabled) {
            Stream<String> tags = state.streamTags().filter((tag)-> {
                for (String tagString : rTags.tags) {
                    if (tagString.startsWith("#")) {
                        tagString = tagString.substring(1);
                        if (tag.id().toString().matches(tagString)) return true;
                    }
                }
                return false;
            }).map((tag) -> "#" + tag.id().toString());
            String id = Registries.BLOCK.getId(state.getBlock()).toString();
            for (String tagString : rTags.tags) {
                if (!tagString.startsWith("#") && id.matches(tagString)) tags = Stream.concat(tags, Stream.of(tagString));
            }
            tagList = tags.toList();
        }

        int i, n = 0, m = 0;

        blocksToBreak.add(pos);
        for (i = 0; i < maxBlocks && i < blocksToBreak.size(); i++) {
            BlockPos currentPos = blocksToBreak.get(i);
            if (blocksToBreak.size() >= maxBlocks) {
                //LOGGER.info("(early return) Tested " + n + " blocks");
                return blocksToBreak;
            }
            testAroundBlock(spreadAccuracy == SimpleConfig.Limits.SPREAD_ACCURACY.ACCURATE ? null : blocksTested, blocksToBreak, maxBlocks, world, state, radius, currentPos, 0, isDebug, tagList, chainReactions, player, getConfig().restrictions);
        }
        //LOGGER.info("Tested " + blocksTested.size() + " blocks");
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
        SimpleConfig config = getConfig();

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

    public static int getVeinminingRadius(PlayerEntity player) {
        int serverRadius = getConfig().limits.radius;
        int playerRadius = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? SimpleVeinminerClient.getConfig().clientRadius : playersRadius.getOrDefault(player.getUuid(), serverRadius);
        if (playerRadius <= 0) playerRadius = 1;
        return Math.min(playerRadius, serverRadius);
    }

    public static SimpleConfig.Limits.SPREAD_ACCURACY getSpreadAccuracy() {
        return getConfig().limits.spreadAccuracy;
    }

    public static boolean isDebug() {
        return getConfig().debug;
    }

    private static boolean isSameBlock(BlockState state, BlockState compareTo, @Nullable List<String> tagList, @Nullable PlayerEntity player, @Nullable SimpleConfig.Restrictions restrictions) {
        if (state.getBlock().equals(compareTo.getBlock())) return true;
        if (state.isAir() || compareTo.isAir()) return false;
        if (player != null && restrictions != null && !canVeinmine(player, null, null, state, restrictions)) return false;
        if (tagList != null) {
            String id = Registries.BLOCK.getId(state.getBlock()).toString();
            for (String tag : tagList) {
                if (tag.startsWith("#")) {
                    Identifier identifier = new Identifier(tag.substring(1));
                    if (state.isIn(TagKey.of(Registries.BLOCK.getKey(), identifier))) return true;
                } else if (id.matches(tag)) return true;
            }
        }
        return false;
    }

    private static boolean listIncludes(Block block, SimpleConfig.Restrictions restrictions) {
        for (int i = 0; i < restrictions.restrictionList.list.size(); i++) {
            String name = restrictions.restrictionList.list.get(i);
            Identifier identifier = new Identifier(name.replaceAll("[^a-z0-9-_:/]", ""));
            if (name.startsWith("#")) {
                if (block.getDefaultState().isIn(TagKey.of(Registries.BLOCK.getKey(), identifier))) return true;
            } else {
                Block compareTo = Registries.BLOCK.get(identifier);
                if (compareTo.equals(block)) return true;
            }
        }
        return false;
    }

    public static boolean canVeinmine(PlayerEntity player, @Nullable World world, @Nullable BlockPos pos, BlockState state, SimpleConfig.Restrictions restrictions) {
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

        // if (sendMessage) player.sendMessage(Text.of(""), true);

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

    private static void setPlayerVeinminingRadius(PlayerEntity player, int radius) {
        playersRadius.put(player.getUuid(), radius);
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
        playersRadius = new HashMap<>();
        ServerPlayNetworking.registerGlobalReceiver(Constants.NETWORKING_VEINMINE, (server, player, handler, buf, sender) -> {
            boolean isVeinMining = buf.readBoolean();
            server.execute(() -> setVeinmining(player, isVeinMining));
        });
        ServerPlayNetworking.registerGlobalReceiver(Constants.NETWORKING_RADIUS, (server, player, handler, buf, sender) -> {
            int radius = buf.readInt();
            server.execute(() -> setPlayerVeinminingRadius(player, radius));
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
            // LOGGER.info("New server");
        });

        new ArgumentTypes();
        new CommandRegister();

        LOGGER.info("Simple VeinMiner initialized");
    }
}
