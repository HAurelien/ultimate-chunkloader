package com.habermacheraurelien.ultimatechunkloader.util;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.ChunkAnchorHandler;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.PlayerTracker;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber
public class ServerEventHandling {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // This is where you load your data when the server starts
        MinecraftServer server = event.getServer();

        // Example: Loading saved data
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();

        ListChunkAnchorSavedData chunkSavedData = dataStorage.computeIfAbsent(
                ListChunkAnchorSavedData.FACTORY,
                ListChunkAnchorSavedData.DATA_NAME
        );

        ListPlayerDiscoveredAnchorSavedData playerSavedData = dataStorage.computeIfAbsent(
                ListPlayerDiscoveredAnchorSavedData.FACTORY,
                ListPlayerDiscoveredAnchorSavedData.DATA_NAME
        );

        ChunkAnchorHandler.setChunkAnchorBlockArraySavedData(chunkSavedData);
        PlayerTracker.setListPlayerDiscoveredAnchorSavedData(playerSavedData);

        ChunkAnchorHandler.init(server);
    }
}
