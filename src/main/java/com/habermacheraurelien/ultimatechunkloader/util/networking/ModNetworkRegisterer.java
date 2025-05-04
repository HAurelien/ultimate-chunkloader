package com.habermacheraurelien.ultimatechunkloader.util.networking;

import com.habermacheraurelien.ultimatechunkloader.util.networking.handlers.ModClientPayloadHandler;
import com.habermacheraurelien.ultimatechunkloader.util.networking.handlers.ModServerPayloadHandler;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponseScreenAnchorTrackerModel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworkRegisterer {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                RequestForScreenAnchorTrackerModel.TYPE,
                RequestForScreenAnchorTrackerModel.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestPlayerAnchorList,
                        ModServerPayloadHandler::handleRequestPlayerAnchorList
                )
        );

        registrar.playBidirectional(
                ResponseScreenAnchorTrackerModel.TYPE,
                ResponseScreenAnchorTrackerModel.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleReceivingPlayerAnchorList,
                        ModServerPayloadHandler::handleReceivingPlayerAnchorList
                )
        );


        registrar.playBidirectional(
                RequestChangeAnchorStatus.TYPE,
                RequestChangeAnchorStatus.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestChangeAnchorStatus,
                        ModServerPayloadHandler::handleRequestChangeAnchorStatus
                )
        );
    }
}
