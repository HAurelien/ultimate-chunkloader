package com.habermacheraurelien.ultimatechunkloader.model;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAnchorTrackerModel {
    private final Player player;
    private final List<Integer> chunksDiscovered = new ArrayList<>(); // TODO: Load from save

    public PlayerAnchorTrackerModel(Player _player) {
        this.player = _player;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean contains(Integer anchorId){
        return chunksDiscovered.contains(anchorId);
    }

    public void addAnchor(Integer anchorId){
        if(!chunksDiscovered.contains(anchorId)){
            chunksDiscovered.add(anchorId);
        }
    }

    public void removeAnchor(Integer anchorId){
        chunksDiscovered.remove(anchorId);
    }

    public void clearAnchors(){
        chunksDiscovered.clear();
    }

    public List<Integer> getIdList(){
        return chunksDiscovered;
    }
}
