package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class PlayerTracker {
    private final static String SAVE_NAME = UltimateChunkLoaderMod.MOD_ID + ":" + "player_tracker";

    //private final static List<PlayerAnchorTrackerModel> list_player_anchor = new ArrayList<>();

    public static ListPlayerDiscoveredAnchorSavedData listPlayerDiscoveredAnchorSavedData = null;

    public static void setListPlayerDiscoveredAnchorSavedData(ListPlayerDiscoveredAnchorSavedData savedData){
        listPlayerDiscoveredAnchorSavedData = savedData;
    }

    public static void addBlock(Integer blockId, Level level, UUID playerId){
        if(!level.isClientSide){
            PlayerAnchorTrackerModel tracker = listPlayerDiscoveredAnchorSavedData
                    .getPlayerAnchorFromPlayer(playerId);
            tracker.addAnchor(blockId);
        }
    }

    public static PlayerAnchorTrackerModel getAllIdsDiscoveredByPlayer(Level level, UUID playerId){
        return listPlayerDiscoveredAnchorSavedData.getPlayerAnchorFromPlayer(playerId);
    }

    public static void removeBlockFromAllPlayers(ChunkAnchorBlockModel chunkAnchorBlockModel) {
        listPlayerDiscoveredAnchorSavedData.removeFromAllPlayers(chunkAnchorBlockModel.getId());
    }
}
