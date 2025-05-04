package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers.AnchorListDataHolder;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponseScreenAnchorTrackerModel;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModClientPayloadHandler {
    public static void handleReceivingPlayerAnchorList(final ResponseScreenAnchorTrackerModel responseScreenAnchorTrackerModel, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload a received CS, payload :" +
                responseScreenAnchorTrackerModel.toString()));
        AnchorListDataHolder.setScreenAnchorTrackerModel(
                responseScreenAnchorTrackerModel.screenAnchorTrackerModel());
    }

    public static void handleRequestPlayerAnchorList(final RequestForScreenAnchorTrackerModel requestForScreenAnchorTrackerModel, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload r received CS, ID : " +
                requestForScreenAnchorTrackerModel.playerUUID()));
    }

    public static void handleRequestChangeAnchorStatus(final RequestChangeAnchorStatus requestChangeAnchorStatus, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload r received CS, ID : " +
                requestChangeAnchorStatus.anchorId()));
    }
}
