package com.habermacheraurelien.ultimatechunkloader.GUI.actionHandlers;

import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import net.neoforged.neoforge.network.PacketDistributor;

public class AnchorListActionsHandler {
    public static void onAnchorUpdateState(Integer anchorId){
        PacketDistributor.sendToServer(new RequestChangeAnchorStatus(anchorId));
    }
}
