package com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.ModUpdatableScreen;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;

import java.util.ArrayList;
import java.util.List;

public class AnchorListDataHolder {
    private static PlayerAnchorTrackerModel playerAnchorTrackerModel;
    private static final List<ModUpdatableScreen> listeners = new ArrayList<>();

    public static final String ID_PLAYER_ANCHOR_TRACKER_MODEL = "player_anchor_tracker_model";


    public static PlayerAnchorTrackerModel playerAnchorTrackerModel() {
        return playerAnchorTrackerModel;
    }

    public static void setListPlayerDiscoveredAnchorSavedData(PlayerAnchorTrackerModel playerAnchorTrackerModel) {
        AnchorListDataHolder.playerAnchorTrackerModel = playerAnchorTrackerModel;
        onChange();
    }

    public static void onChange(){
        listeners.forEach(listener -> listener.onDataChange(ID_PLAYER_ANCHOR_TRACKER_MODEL));
    }

    public static List<ModUpdatableScreen> getListeners() {
        return listeners;
    }

    public static void addListener(ModUpdatableScreen listener) {
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public static void removeListener(ModUpdatableScreen listener){
        listeners.remove(listener);
    }
}
