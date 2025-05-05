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
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * ModServerPayloadHandler handles server-side logic for processing network payload requests
 * related to player anchors, chunk anchors, and anchor properties. The class provides methods
 * for handling different types of requests such as requesting anchor lists, changing anchor status,
 * modifying anchor properties, and forgetting anchors.
 */
public class ModServerPayloadHandler {

    /**
     * Handles the reception of a {@link ResponseScreenAnchorTrackerModel} from the client.
     * This method currently does nothing but can be extended for additional server-side logic.
     *
     * @param response the response payload received from the client
     * @param context the context of the payload, including the player who sent it
     */
    public static void handleReceivingPlayerAnchorList(final ResponseScreenAnchorTrackerModel response, final IPayloadContext context) {
    }

    /**
     * Handles the {@link RequestForScreenAnchorTrackerModel} sent by the client.
     * This method processes the request by fetching the player's anchor list and sending it back to the client
     * in the form of a {@link ResponseScreenAnchorTrackerModel}.
     *
     * @param request the request payload containing the player's UUID
     * @param context the context of the payload, including the player who sent the request
     */
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

    /**
     * Handles the {@link RequestChangeAnchorStatus} request sent by the client.
     * This method processes the request to change the status of a chunk anchor and broadcasts the update to all players.
     *
     * @param request the request payload containing the anchor ID whose status needs to be changed
     * @param context the context of the payload, including the player who sent the request
     */
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

    /**
     * Broadcasts an update request to all players, refreshing their view of chunk anchors.
     * This method is used after a chunk anchor's status has changed.
     *
     * @param server the Minecraft server
     */
    private static void sendUpdateRequestToAllPlayers(MinecraftServer server) {
        ListPlayerDiscoveredAnchorSavedData discoveredData = DataManager.getListPlayerDiscoveredAnchorSavedData(server);
        ListChunkAnchorSavedData chunkData = DataManager.getListChunkAnchorSavedData(server);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ScreenAnchorTrackerModel model = buildScreenAnchorTrackerModel(player.getUUID(), discoveredData, chunkData);
            PacketDistributor.sendToPlayer(player, new ResponseScreenAnchorTrackerModel(model));
        }
    }

    /**
     * Builds a {@link ScreenAnchorTrackerModel} for a given player, containing a list of chunk anchors
     * with the player-specific names applied to them.
     *
     * @param playerId the UUID of the player whose anchor tracker model is being built
     * @param discoveredData the list of discovered anchors for all players
     * @param chunkData the list of all chunk anchors in the game
     * @return the constructed {@link ScreenAnchorTrackerModel}
     */
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

    /**
     * Handles the {@link RequestChangeAnchorPlayerProperty} request sent by the client.
     * This method processes the request to change the properties of a specific anchor (e.g., its name).
     * If the anchor exists, its properties are updated and the changes are broadcast to all players.
     *
     * @param requestChangeAnchorPlayerProperty the request payload containing the new anchor properties
     * @param iPayloadContext the context of the payload, including the player who sent the request
     */
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

    /**
     * Retrieves a merged list of anchors for a specific player, including the player's customized anchor names.
     *
     * @param player the player whose anchors are being retrieved
     * @param listPlayerDiscoveredAnchorSavedData the list of all player-discovered anchors
     * @param listChunkAnchorSavedData the list of all chunk anchors in the world
     * @return the list of merged anchors for the player, including custom names
     */
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
        // Apply player's name

        return ofPlayer.getAnchorList().stream()
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
    }

    /**
     * Handles the {@link RequestPlayerForgetAnchor} request sent by the client.
     * This method processes the request to remove an anchor from the player's discovered anchors and broadcasts the update.
     *
     * @param requestPlayerForgetAnchor the request payload containing the anchor ID to be forgotten
     * @param iPayloadContext the context of the payload, including the player who sent the request
     */
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
