package com.habermacheraurelien.ultimatechunkloader.GUI.actionHandlers;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorPropertiesModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorPlayerProperty;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestPlayerForgetAnchor;
import net.neoforged.neoforge.network.PacketDistributor;


/**
 * Handles UI-triggered actions related to anchor block models,
 * forwarding requests to the server through appropriate network packets.
 *
 * <p>This class acts as the bridge between client-side UI actions and server-side logic,
 * encapsulating all anchor-related interaction logic.</p>
 */
public class AnchorListActionsHandler {

    /**
     * Sends a request to the server to toggle the active state of the given anchor.
     *
     * @param anchor the anchor whose state should be toggled
     */
    public static void onAnchorUpdateState(ChunkAnchorBlockModel anchor) {
        PacketDistributor.sendToServer(new RequestChangeAnchorStatus(anchor.getId()));
    }

    /**
     * Sends an update to the server when one of the player's editable properties
     * of an anchor has been changed (e.g. the name).
     *
     * @param anchor the anchor with updated properties
     */
    public static void onAnchorChangeProperty(ChunkAnchorBlockModel anchor) {
        PacketDistributor.sendToServer(
                new RequestChangeAnchorPlayerProperty(
                        new PlayerAnchorPropertiesModel(anchor.getId(), anchor.getName())
                )
        );
    }

    /**
     * Sends a request to the server asking for the given anchor to be "forgotten"
     * by the player, effectively removing it from the visible list on the client.
     *
     * @param anchor the anchor to remove
     */
    public static void onAnchorRemove(ChunkAnchorBlockModel anchor) {
        PacketDistributor.sendToServer(new RequestPlayerForgetAnchor(anchor.getId()));
    }
}