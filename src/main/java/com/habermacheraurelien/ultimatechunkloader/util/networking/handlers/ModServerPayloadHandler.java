package com.habermacheraurelien.ultimatechunkloader.util.networking.handlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.model.ScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.ChunkAnchorHandler;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.DataManager;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestChangeAnchorStatus;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.ResponseScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public class ModServerPayloadHandler {
    public static void handleReceivingPlayerAnchorList(final ResponseScreenAnchorTrackerModel responseScreenAnchorTrackerModel, final IPayloadContext context){
        context.player().sendSystemMessage(Component.literal( "Payload a received SS, size : " +
                responseScreenAnchorTrackerModel.screenAnchorTrackerModel().getChunkAnchorBlockModelList().size()));
    }

    public static void handleRequestPlayerAnchorList(final RequestForScreenAnchorTrackerModel requestForScreenAnchorTrackerModel, final IPayloadContext context){
        ServerPlayer player = (ServerPlayer) context.player();
        MinecraftServer server = player.getServer();

        player.sendSystemMessage(Component.literal( "Payload r received SS, ID : " +
                requestForScreenAnchorTrackerModel.playerUUID()));
        UUID id = UUID.fromString(requestForScreenAnchorTrackerModel.playerUUID());

        if(server == null){
            context.player().sendSystemMessage(
                    Component.literal( "Error while handling the request for the list of anchors with id : "
                            + requestForScreenAnchorTrackerModel.playerUUID()));
            return;
        }

        PlayerAnchorTrackerModel playerAnchorTrackerModel =
                DataManager.getListPlayerDiscoveredAnchorSavedData(server).getPlayerAnchorFromPlayer(id);

        ListChunkAnchorSavedData listChunkAnchorSavedData = DataManager.getListChunkAnchorSavedData(server);

        ScreenAnchorTrackerModel screenAnchorTrackerModel = new ScreenAnchorTrackerModel(listChunkAnchorSavedData
                .getChunkAnchorBlockArrayList().stream().filter(
                chunkAnchorBlockModel ->
                        playerAnchorTrackerModel.getIdList().contains(chunkAnchorBlockModel.getId())
        ).toList());

        PacketDistributor.sendToPlayer(player, new ResponseScreenAnchorTrackerModel(screenAnchorTrackerModel));
    }
    public static void handleRequestChangeAnchorStatus(final RequestChangeAnchorStatus requestChangeAnchorStatus, final IPayloadContext context){
        ServerPlayer player = (ServerPlayer) context.player();
        MinecraftServer server = player.getServer();

        player.sendSystemMessage(Component.literal( "Payload r received SS, ID : " +
                requestChangeAnchorStatus.anchorId()));

        if(server == null){
            context.player().sendSystemMessage(
                    Component.literal( "Error while handling the request for the list of anchors with id : "
                            + requestChangeAnchorStatus.anchorId()));
            return;
        }

        ChunkAnchorHandler.changeAnchorStatus(requestChangeAnchorStatus.anchorId());
        sendUpdateRequestToAllPlayers(server, server.getPlayerList().getPlayers());
    }

    private static void sendUpdateRequestToAllPlayers(MinecraftServer server, List<ServerPlayer> players){
        ListPlayerDiscoveredAnchorSavedData listPlayerDiscoveredAnchorSavedData =
                DataManager.getListPlayerDiscoveredAnchorSavedData(server);
        ListChunkAnchorSavedData listChunkAnchorSavedData =
                DataManager.getListChunkAnchorSavedData(server);

        players.forEach(player -> PacketDistributor.sendToPlayer(player,
                new ResponseScreenAnchorTrackerModel(
                        getNewScreenAnchorTrackerModel(player, listPlayerDiscoveredAnchorSavedData,
                                listChunkAnchorSavedData))));

    }

    private static ScreenAnchorTrackerModel getNewScreenAnchorTrackerModel(MinecraftServer server, ServerPlayer player){
        List<Integer> playerAnchorRegistered =
                DataManager.getListPlayerDiscoveredAnchorSavedData(server)
                        .getPlayerAnchorFromPlayer(player.getUUID())
                        .getIdList();
        List<ChunkAnchorBlockModel> unfilteredList = DataManager
                .getListChunkAnchorSavedData(server)
                .getChunkAnchorBlockArrayList();

        List<ChunkAnchorBlockModel> chunkAnchorBlockModelList = unfilteredList.stream()
                .filter(chunkAnchorBlockModel ->
                        playerAnchorRegistered.contains(chunkAnchorBlockModel.getId())).toList();

        return new ScreenAnchorTrackerModel(chunkAnchorBlockModelList);
    }

    private static ScreenAnchorTrackerModel getNewScreenAnchorTrackerModel(ServerPlayer player,
                                                ListPlayerDiscoveredAnchorSavedData listPlayerDiscoveredAnchorSavedData,
                                                ListChunkAnchorSavedData listChunkAnchorSavedData){

        List<Integer> playerAnchorRegistered =
                listPlayerDiscoveredAnchorSavedData.getPlayerAnchorFromPlayer(player.getUUID()).getIdList();

        List<ChunkAnchorBlockModel> unfilteredList = listChunkAnchorSavedData.getChunkAnchorBlockArrayList();

        List<ChunkAnchorBlockModel> chunkAnchorBlockModelList = unfilteredList.stream()
                .filter(chunkAnchorBlockModel ->
                        playerAnchorRegistered.contains(chunkAnchorBlockModel.getId())).toList();

        return new ScreenAnchorTrackerModel(chunkAnchorBlockModelList);
    }
}
