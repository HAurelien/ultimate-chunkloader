package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.model.ScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.ChunkAnchorHandler;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorPropertiesModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.DataManager;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.*;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
public class ModServerPayloadHandler {

    public static void handleReceivingPlayerAnchorList(final ResponseScreenAnchorTrackerModel response, final IPayloadContext context) {
    }

    public static void handleRequestPlayerAnchorList(final RequestForScreenAnchorTrackerModel request, final IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        MinecraftServer server = player.getServer();

        if (server == null) {
            context.player().sendSystemMessage(Component.literal("Error: No server instance found."));
            return;
        }

        UUID requestedPlayerId = UUID.fromString(request.playerUUID());

        ListPlayerDiscoveredAnchorSavedData discoveredData = DataManager.getListPlayerDiscoveredAnchorSavedData(server);
        ListChunkAnchorSavedData chunkData = DataManager.getListChunkAnchorSavedData(server);

        ScreenAnchorTrackerModel trackerModel = buildScreenAnchorTrackerModel(requestedPlayerId, discoveredData, chunkData);
        PacketDistributor.sendToPlayer(player, new ResponseScreenAnchorTrackerModel(trackerModel));
    }

    public static void handleRequestChangeAnchorStatus(final RequestChangeAnchorStatus request, final IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        MinecraftServer server = player.getServer();

        if (server == null) {
            context.player().sendSystemMessage(Component.literal("Error: No server instance found while processing anchor status change."));
            return;
        }

        ChunkAnchorHandler.changeAnchorStatus(server, request.anchorId());
        sendUpdateRequestToAllPlayers(server);
    }

    private static void sendUpdateRequestToAllPlayers(MinecraftServer server) {
        ListPlayerDiscoveredAnchorSavedData discoveredData = DataManager.getListPlayerDiscoveredAnchorSavedData(server);
        ListChunkAnchorSavedData chunkData = DataManager.getListChunkAnchorSavedData(server);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ScreenAnchorTrackerModel model = buildScreenAnchorTrackerModel(player.getUUID(), discoveredData, chunkData);
            PacketDistributor.sendToPlayer(player, new ResponseScreenAnchorTrackerModel(model));
        }
    }

    private static ScreenAnchorTrackerModel buildScreenAnchorTrackerModel(UUID playerId,
                                                                          ListPlayerDiscoveredAnchorSavedData discoveredData,
                                                                          ListChunkAnchorSavedData chunkData) {
        PlayerAnchorTrackerModel playerTracker = discoveredData.getPlayerAnchorFromPlayer(playerId);
        Collection<PlayerAnchorPropertiesModel> playerAnchors = playerTracker.getAnchorList();

        // Map of ID -> Player-defined name
        Map<Integer, String> playerAnchorNames = playerAnchors.stream()
                .collect(Collectors.toMap(PlayerAnchorPropertiesModel::getId, PlayerAnchorPropertiesModel::getName));

        // Map of ID -> ChunkAnchorBlockModel
        Map<Integer, ChunkAnchorBlockModel> allAnchorsById = chunkData.getChunkAnchorBlockArrayList().stream()
                .collect(Collectors.toMap(ChunkAnchorBlockModel::getId, Function.identity()));

        // Filter and apply name
        List<ChunkAnchorBlockModel> personalizedAnchors = playerAnchorNames.entrySet().stream()
                .map(entry -> {
                    ChunkAnchorBlockModel block = allAnchorsById.get(entry.getKey());
                    if (block != null) {
                        block.setName(entry.getValue());
                        return block;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        return new ScreenAnchorTrackerModel(personalizedAnchors);
    }

    public static void handleRequestChangeAnchorProperty(RequestChangeAnchorPlayerProperty requestChangeAnchorPlayerProperty, IPayloadContext iPayloadContext) {
        ServerPlayer player = (ServerPlayer) iPayloadContext.player();
        MinecraftServer server = player.getServer();

        if (server == null) {
            player.sendSystemMessage(Component.literal("Error: No server instance found while processing anchor property change."));
            return;
        }

        UUID playerId = player.getUUID();
        PlayerAnchorPropertiesModel newProperties = requestChangeAnchorPlayerProperty.anchorProperties();

        // Retrieve the saved data for the player
        ListPlayerDiscoveredAnchorSavedData playerData = DataManager.getListPlayerDiscoveredAnchorSavedData(server);
        PlayerAnchorTrackerModel playerAnchorTracker = playerData.getPlayerAnchorFromPlayer(playerId);

        // Find the anchor to update by its ID
        PlayerAnchorPropertiesModel existingAnchor = playerAnchorTracker.getAnchorList().stream()
                .filter(anchor -> anchor.getId().equals(newProperties.getId()))
                .findFirst()
                .orElse(null);

        // If the anchor exists, update its properties
        if (existingAnchor != null) {
            existingAnchor.setName(newProperties.getName());  // You can add more properties if needed

            // Mark the data as changed (so it will be saved)
            playerData.setDirty();

            // Optionally, broadcast the update to all players (this could be relevant for world-wide updates)
            ScreenAnchorTrackerModel screenAnchorTrackerModel = buildScreenAnchorTrackerModel(player.getUUID(),
                    playerData, DataManager.getListChunkAnchorSavedData(server));
            PacketDistributor.sendToPlayer(player, new ResponseScreenAnchorTrackerModel(screenAnchorTrackerModel));

            // Acknowledge the player (optional: feedback message)
        } else {
            // If the anchor doesn't exist, notify the player
            player.sendSystemMessage(Component.literal("Error: Anchor with ID " + newProperties.getId() + " not found."));
        }
    }
    private static List<ChunkAnchorBlockModel> getMergedListOfAnchors(ServerPlayer player,
                                                                      ListPlayerDiscoveredAnchorSavedData listPlayerDiscoveredAnchorSavedData,
                                                                      ListChunkAnchorSavedData listChunkAnchorSavedData){

        PlayerAnchorTrackerModel ofPlayer = listPlayerDiscoveredAnchorSavedData
                .getPlayerAnchorFromPlayer(player.getUUID());

        List<ChunkAnchorBlockModel> unfilteredList = listChunkAnchorSavedData.getChunkAnchorBlockArrayList();

        // Step 1: Build a Map from ID -> ChunkAnchorBlockModel for fast lookup
        Map<Integer, ChunkAnchorBlockModel> idToBlockMap = unfilteredList.stream()
                .collect(Collectors.toMap(ChunkAnchorBlockModel::getId, Function.identity()));

        // Step 2: Filter and apply names in one pass
        List<ChunkAnchorBlockModel> filteredList = ofPlayer.getAnchorList().stream()
                .map(playerAnchor -> {
                    ChunkAnchorBlockModel block = idToBlockMap.get(playerAnchor.getId());
                    if (block != null) {
                        block.setName(playerAnchor.getName()); // Apply player's name
                        return block;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        return filteredList;
    }

    public static void handleRequestPlayerForgetAnchor(RequestPlayerForgetAnchor requestPlayerForgetAnchor, IPayloadContext iPayloadContext) {
        ServerPlayer player = (ServerPlayer) iPayloadContext.player();
        MinecraftServer server = player.getServer();

        if (server == null) {
            player.sendSystemMessage(Component.literal("Error: No server instance found while processing anchor " +
                    "forget from player."));
            return;
        }
        ListPlayerDiscoveredAnchorSavedData playerData = DataManager.getListPlayerDiscoveredAnchorSavedData(server);
        playerData.forgetAnchorForPlayer(player, requestPlayerForgetAnchor.anchorId());

        ScreenAnchorTrackerModel screenAnchorTrackerModel = buildScreenAnchorTrackerModel(player.getUUID(),
                playerData, DataManager.getListChunkAnchorSavedData(server));
        PacketDistributor.sendToPlayer(player, new ResponseScreenAnchorTrackerModel(screenAnchorTrackerModel));
    }
}
