package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers.AnchorListDataHolder;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponseScreenAnchorTrackerModel;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * ModClientPayloadHandler handles client-side logic for processing network payload requests
 * related to player anchors, chunk anchors, and anchor properties. The class provides methods
 * for handling different types of requests and responses, such as receiving anchor lists, changing anchor
 * status or properties, and handling player anchor removal.
 */
public class ModClientPayloadHandler {

    /**
     * Handles the {@link ResponseScreenAnchorTrackerModel} received from the server.
     * This method updates the client-side anchor tracker model by storing it in the
     * {@link AnchorListDataHolder}.
     *
     * @param responseScreenAnchorTrackerModel the response payload containing the updated screen anchor tracker model
     * @param context the context of the payload, including the player who received it
     */
    public static void handleReceivingPlayerAnchorList(final ResponseScreenAnchorTrackerModel responseScreenAnchorTrackerModel, final IPayloadContext context) {

        // Update the client-side anchor list with the received model
        AnchorListDataHolder.setScreenAnchorTrackerModel(
                responseScreenAnchorTrackerModel.screenAnchorTrackerModel());
    }

    /**
     * Handles the {@link RequestForScreenAnchorTrackerModel} request sent by the server.
     * Currently, this method does not perform any actions but can be extended in the future
     * to handle server requests related to the player's anchor list.
     *
     * @param requestForScreenAnchorTrackerModel the request payload containing the player's UUID
     * @param context the context of the payload, including the player who sent the request
     */
    public static void handleRequestPlayerAnchorList(final RequestForScreenAnchorTrackerModel requestForScreenAnchorTrackerModel, final IPayloadContext context) {
        // No action for now, but could be used in the future to handle requests for player anchor lists
    }

    /**
     * Handles the {@link RequestChangeAnchorStatus} request sent by the server.
     * Currently, this method does not perform any actions but can be extended in the future
     * to handle requests to change anchor statuses on the client side.
     *
     * @param requestChangeAnchorStatus the request payload containing the anchor ID whose status needs to be changed
     * @param context the context of the payload, including the player who sent the request
     */
    public static void handleRequestChangeAnchorStatus(final RequestChangeAnchorStatus requestChangeAnchorStatus, final IPayloadContext context) {
        // No action for now, but could be used in the future to handle anchor status change requests
    }

    /**
     * Handles the {@link CustomPacketPayload} request for changing anchor properties.
     * Currently, this method does not perform any actions but can be extended to handle
     * changes in anchor properties (e.g., anchor name) from the server.
     *
     * @param customPacketPayload the custom packet payload containing the anchor properties to be updated
     * @param iPayloadContext the context of the payload, including the player who sent the request
     */
    public static void handleRequestChangeAnchorProperty(CustomPacketPayload customPacketPayload, IPayloadContext iPayloadContext) {
        // No action for now, but could be extended in the future to handle anchor property changes
    }

    /**
     * Handles the {@link CustomPacketPayload} request for a player to forget an anchor.
     * Currently, this method does not perform any actions but can be extended to handle the removal
     * of anchors from the player's discovered list on the client side.
     *
     * @param customPacketPayload the custom packet payload containing the anchor ID to be forgotten
     * @param iPayloadContext the context of the payload, including the player who sent the request
     */
    public static void handleRequestPlayerForgetAnchor(CustomPacketPayload customPacketPayload, IPayloadContext iPayloadContext) {
        // No action for now, but could be extended in the future to handle forgetting anchors
    }
}

