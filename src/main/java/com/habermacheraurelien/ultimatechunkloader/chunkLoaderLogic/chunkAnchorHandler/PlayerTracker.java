package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerTracker {
    private final static List<PlayerAnchorTrackerModel> list_player_anchor = new ArrayList<>();

    public static void addBlock(Integer blockId, Level level, Player player){
        if(!level.isClientSide){
            Optional<PlayerAnchorTrackerModel> optionalTracker = list_player_anchor.stream()
                    .filter(playerAnchorTrackerModel ->
                            playerAnchorTrackerModel.getPlayer().equals(player)).findFirst();
            if(optionalTracker.isPresent()){
                optionalTracker.get().addAnchor(blockId);
            }
            else {
                PlayerAnchorTrackerModel newTracker = new PlayerAnchorTrackerModel(player);
                newTracker.addAnchor(blockId);
                list_player_anchor.add(newTracker);
            }
        }
    }

    public static Optional<PlayerAnchorTrackerModel> getAllIdsDiscoveredByPlayer(Level level, Player player){
        return list_player_anchor.stream().filter(playerAnchorTrackerModel ->
                playerAnchorTrackerModel.getPlayer().equals(player)).findFirst();
    }
}
