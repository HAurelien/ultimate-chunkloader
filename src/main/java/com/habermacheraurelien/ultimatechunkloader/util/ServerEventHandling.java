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

/**
 * This class handles server events related to server startup and initialization.
 * It subscribes to the Minecraft event bus to listen for specific server events and handle them accordingly.
 */
@EventBusSubscriber
public class ServerEventHandling {

    /**
     * This method is triggered when the server is starting.
     * It is responsible for loading any necessary data during server startup and performing initial setup.
     *
     * @param event the event that is triggered when the server is starting
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Get the Minecraft server instance from the event
        MinecraftServer server = event.getServer();

        // Example: Loading saved data from the server's world data storage
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();

        // Load or create chunk anchor data storage
        ListChunkAnchorSavedData chunkSavedData = dataStorage.computeIfAbsent(
                ListChunkAnchorSavedData.FACTORY,
                ListChunkAnchorSavedData.DATA_NAME
        );

        // Load or create player discovered anchor data storage
        ListPlayerDiscoveredAnchorSavedData playerSavedData = dataStorage.computeIfAbsent(
                ListPlayerDiscoveredAnchorSavedData.FACTORY,
                ListPlayerDiscoveredAnchorSavedData.DATA_NAME
        );

        // Set the chunk anchor data handler
        ChunkAnchorHandler.setChunkAnchorBlockArraySavedData(chunkSavedData);

        // Set the player discovered anchor data handler
        PlayerTracker.setListPlayerDiscoveredAnchorSavedData(playerSavedData);

        // Initialize chunk anchor handler
        ChunkAnchorHandler.init(server);
    }
}
