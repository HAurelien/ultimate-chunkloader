package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers.AnchorListDataHolder;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForAnchorListPayload;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponsePlayerAnchorListPayload;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModClientPayloadHandler {
    public static void handleReceivingPlayerAnchorList(final ResponsePlayerAnchorListPayload responsePlayerAnchorListPayload, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload answer received client-side ! " +
                responsePlayerAnchorListPayload.playerAnchorTrackerModel().toString()));
        AnchorListDataHolder.setListPlayerDiscoveredAnchorSavedData(responsePlayerAnchorListPayload.playerAnchorTrackerModel());

    }

    public static void handleRequestPlayerAnchorList(final RequestForAnchorListPayload requestForAnchorListPayload, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload request received client-side ! ID : " +
                requestForAnchorListPayload.playerUUID()));
    }
}
