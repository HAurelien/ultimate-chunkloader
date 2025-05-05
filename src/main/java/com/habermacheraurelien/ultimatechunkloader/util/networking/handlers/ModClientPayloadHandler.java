package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers.AnchorListDataHolder;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponseScreenAnchorTrackerModel;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModClientPayloadHandler {
    public static void handleReceivingPlayerAnchorList(final ResponseScreenAnchorTrackerModel responseScreenAnchorTrackerModel, final IPayloadContext context){

        AnchorListDataHolder.setScreenAnchorTrackerModel(
                responseScreenAnchorTrackerModel.screenAnchorTrackerModel());
    }

    public static void handleRequestPlayerAnchorList(final RequestForScreenAnchorTrackerModel requestForScreenAnchorTrackerModel, final IPayloadContext context){

    }

    public static void handleRequestChangeAnchorStatus(final RequestChangeAnchorStatus requestChangeAnchorStatus, final IPayloadContext context){

    }

    public static void handleRequestChangeAnchorProperty(CustomPacketPayload customPacketPayload, IPayloadContext iPayloadContext) {
    }

    public static void handleRequestPlayerForgetAnchor(CustomPacketPayload customPacketPayload, IPayloadContext iPayloadContext) {
    }
}
