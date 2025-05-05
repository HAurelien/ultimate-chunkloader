package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.world.level.Level;

import java.util.UUID;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * This class tracks which anchors have been discovered by which players in the game.
 * It manages saving and retrieving player anchor discovery data and allows for updating player anchor lists.
 */
public class PlayerTracker {

    // The saved data that holds the list of anchors discovered by each player.
    public static ListPlayerDiscoveredAnchorSavedData listPlayerDiscoveredAnchorSavedData = null;

    /**
     * Sets the saved data for discovered anchors for players.
     *
     * @param savedData The data containing the discovered anchors for each player.
     */
    public static void setListPlayerDiscoveredAnchorSavedData(ListPlayerDiscoveredAnchorSavedData savedData){
        listPlayerDiscoveredAnchorSavedData = savedData;
    }

    /**
     * Adds a block (anchor) to the list of discovered anchors for the given player.
     *
     * @param blockId The ID of the block (anchor) to be added.
     * @param level The level where the anchor is located.
     * @param playerId The UUID of the player discovering the anchor.
     */
    public static void addBlock(Integer blockId, Level level, UUID playerId){
        // Ensure the action is only performed on the server-side.
        if(!level.isClientSide){
            // Retrieve the player's anchor tracker and add the discovered block ID to it.
            PlayerAnchorTrackerModel tracker = listPlayerDiscoveredAnchorSavedData
                    .getPlayerAnchorFromPlayer(playerId);
            tracker.addAnchor(blockId);
            // Mark the saved data as dirty to ensure it gets saved later.
            listPlayerDiscoveredAnchorSavedData.setDirty();
        }
    }

    /**
     * Retrieves all discovered anchor IDs for a specific player.
     *
     * @param level The level where the player is located.
     * @param playerId The UUID of the player.
     * @return The PlayerAnchorTrackerModel that contains all the anchor IDs discovered by the player.
     */
    public static PlayerAnchorTrackerModel getAllIdsDiscoveredByPlayer(Level level, UUID playerId){
        // Retrieve and return the player's anchor tracker from the saved data.
        return listPlayerDiscoveredAnchorSavedData.getPlayerAnchorFromPlayer(playerId);
    }

    /**
     * Removes a chunk anchor block from the discovery lists of all players.
     *
     * @param chunkAnchorBlockModel The chunk anchor block model to be removed from all players' discovery lists.
     */
    public static void removeBlockFromAllPlayers(ChunkAnchorBlockModel chunkAnchorBlockModel) {
        // Remove the anchor block from the discovery lists of all players.
        listPlayerDiscoveredAnchorSavedData.removeFromAllPlayers(chunkAnchorBlockModel.getId());
    }
}
