package com.habermacheraurelien.ultimatechunkloader.util.networking;

import com.habermacheraurelien.ultimatechunkloader.util.networking.handlers.ModClientPayloadHandler;
import com.habermacheraurelien.ultimatechunkloader.util.networking.handlers.ModServerPayloadHandler;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForAnchorListPayload;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponsePlayerAnchorListPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworkRegisterer {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                RequestForAnchorListPayload.TYPE,
                RequestForAnchorListPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestPlayerAnchorList,
                        ModServerPayloadHandler::handleRequestPlayerAnchorList
                )
        );

        registrar.playBidirectional(
                ResponsePlayerAnchorListPayload.TYPE,
                ResponsePlayerAnchorListPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleReceivingPlayerAnchorList,
                        ModServerPayloadHandler::handleReceivingPlayerAnchorList
                )
        );
    }
}
