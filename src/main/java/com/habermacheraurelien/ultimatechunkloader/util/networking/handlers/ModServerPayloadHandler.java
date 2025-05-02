package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.DataManager;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForAnchorListPayload;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponsePlayerAnchorListPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class ModServerPayloadHandler {
    public static void handleReceivingPlayerAnchorList(final ResponsePlayerAnchorListPayload responsePlayerAnchorListPayload, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload request received server-side ! Size : " +
                responsePlayerAnchorListPayload.playerAnchorTrackerModel().getIdList().size()));
    }

    public static void handleRequestPlayerAnchorList(final RequestForAnchorListPayload requestForAnchorListPayload, final IPayloadContext context){
        ServerPlayer player = (ServerPlayer) context.player();
        MinecraftServer server = player.getServer();

        player.sendSystemMessage(Component.literal( "Payload request received server-side ! ID : " +
                requestForAnchorListPayload.playerUUID()));
        UUID id = UUID.fromString(requestForAnchorListPayload.playerUUID());

        if(server == null){
            context.player().sendSystemMessage(
                    Component.literal( "Error while handling the request for the list of anchors with id : "
                            + requestForAnchorListPayload.playerUUID()));
            return;
        }

        PlayerAnchorTrackerModel playerAnchorTrackerModel =
                DataManager.getListPlayerDiscoveredAnchorSavedData(server).getPlayerAnchorFromPlayer(id);

        PacketDistributor.sendToPlayer(player, new ResponsePlayerAnchorListPayload(playerAnchorTrackerModel));
    }
}
