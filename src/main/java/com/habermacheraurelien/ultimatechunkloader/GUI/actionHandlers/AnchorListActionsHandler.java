package com.habermacheraurelien.ultimatechunkloader.GUI.actionHandlers;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorPropertiesModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorPlayerProperty;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestPlayerForgetAnchor;
import net.neoforged.neoforge.network.PacketDistributor;

public class AnchorListActionsHandler {
    public static void onAnchorUpdateState(ChunkAnchorBlockModel anchor){
        PacketDistributor.sendToServer(new RequestChangeAnchorStatus(anchor.getId()));
    }

    public static void onAnchorChangeProperty(ChunkAnchorBlockModel anchor){

        PacketDistributor.sendToServer(new RequestChangeAnchorPlayerProperty(
                new PlayerAnchorPropertiesModel(anchor.getId(), anchor.getName())
        ));
    }

    public static void onAnchorRemove(ChunkAnchorBlockModel anchor) {
        PacketDistributor.sendToServer(new RequestPlayerForgetAnchor(anchor.getId()));
    }
}
