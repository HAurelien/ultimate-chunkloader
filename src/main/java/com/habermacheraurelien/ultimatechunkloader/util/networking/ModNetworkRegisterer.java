package com.habermacheraurelien.ultimatechunkloader.util.networking;

import com.habermacheraurelien.ultimatechunkloader.util.networking.handlers.ModClientPayloadHandler;
import com.habermacheraurelien.ultimatechunkloader.util.networking.handlers.ModServerPayloadHandler;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
/**
 * ModNetworkRegisterer is responsible for registering network payload handlers in the mod.
 * These handlers define how the mod will handle different types of network messages between the client and server.
 * It subscribes to the {@link RegisterPayloadHandlersEvent} and registers bidirectional payload handlers
 * for various types of requests and responses related to chunk anchors and player anchor properties.
 */
public class ModNetworkRegisterer {

    /**
     * Registers the network payload handlers when the {@link RegisterPayloadHandlersEvent} is fired.
     * The method binds various message types to their respective payload handlers, enabling the communication
     * between the client and server.
     *
     * Each handler is bidirectional, meaning the same handler function can process messages both from
     * the client to the server and from the server to the client.
     *
     * The following message types are registered:
     * <ul>
     *     <li>{@link RequestForScreenAnchorTrackerModel}</li>
     *     <li>{@link ResponseScreenAnchorTrackerModel}</li>
     *     <li>{@link RequestChangeAnchorStatus}</li>
     *     <li>{@link RequestChangeAnchorPlayerProperty}</li>
     *     <li>{@link RequestPlayerForgetAnchor}</li>
     * </ul>
     *
     * @param event the event that triggers the payload handler registration
     */
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event){
        // Register the payload handler for RequestForScreenAnchorTrackerModel
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                RequestForScreenAnchorTrackerModel.TYPE,
                RequestForScreenAnchorTrackerModel.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestPlayerAnchorList,
                        ModServerPayloadHandler::handleRequestPlayerAnchorList
                )
        );

        // Register the payload handler for ResponseScreenAnchorTrackerModel
        registrar.playBidirectional(
                ResponseScreenAnchorTrackerModel.TYPE,
                ResponseScreenAnchorTrackerModel.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleReceivingPlayerAnchorList,
                        ModServerPayloadHandler::handleReceivingPlayerAnchorList
                )
        );

        // Register the payload handler for RequestChangeAnchorStatus
        registrar.playBidirectional(
                RequestChangeAnchorStatus.TYPE,
                RequestChangeAnchorStatus.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestChangeAnchorStatus,
                        ModServerPayloadHandler::handleRequestChangeAnchorStatus
                )
        );

        // Register the payload handler for RequestChangeAnchorPlayerProperty
        registrar.playBidirectional(
                RequestChangeAnchorPlayerProperty.TYPE,
                RequestChangeAnchorPlayerProperty.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestChangeAnchorProperty,
                        ModServerPayloadHandler::handleRequestChangeAnchorProperty
                )
        );

        // Register the payload handler for RequestPlayerForgetAnchor
        registrar.playBidirectional(
                RequestPlayerForgetAnchor.TYPE,
                RequestPlayerForgetAnchor.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handleRequestPlayerForgetAnchor,
                        ModServerPayloadHandler::handleRequestPlayerForgetAnchor
                )
        );
    }
}
