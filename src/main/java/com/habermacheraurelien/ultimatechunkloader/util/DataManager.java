package com.habermacheraurelien.ultimatechunkloader.util;

import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * The DataManager class is responsible for handling the loading and saving of various saved data
 * related to chunk anchors and player-discovered anchors in a Minecraft server world.
 * It provides methods to retrieve and persist data using Minecraft's DimensionDataStorage.
 */
public class DataManager {

    /**
     * Retrieves the saved chunk anchor data from the server.
     * If no existing data is found, it creates a new instance and saves it.
     *
     * @param server the MinecraftServer instance from which the data is retrieved
     * @return the ListChunkAnchorSavedData, either loaded from storage or newly created
     */
    public static ListChunkAnchorSavedData getListChunkAnchorSavedData(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();

        // Try to load existing data (returns null if not found)
        ListChunkAnchorSavedData data = storage.get(ListChunkAnchorSavedData.FACTORY, ListChunkAnchorSavedData.DATA_NAME);

        // If the data is not found, create new instance and save it
        if (data == null) {
            data = new ListChunkAnchorSavedData();
            storage.set(ListChunkAnchorSavedData.DATA_NAME, data); // Store the newly created data
        }

        return data;
    }

    /**
     * Retrieves the saved player-discovered anchor data from the server.
     * If no existing data is found, it creates a new instance and saves it.
     *
     * @param server the MinecraftServer instance from which the data is retrieved
     * @return the ListPlayerDiscoveredAnchorSavedData, either loaded from storage or newly created
     */
    public static ListPlayerDiscoveredAnchorSavedData getListPlayerDiscoveredAnchorSavedData(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();

        // Try to load existing data (returns null if not found)
        ListPlayerDiscoveredAnchorSavedData data = storage.get(ListPlayerDiscoveredAnchorSavedData.FACTORY, ListPlayerDiscoveredAnchorSavedData.DATA_NAME);

        // If the data is not found, create new instance and save it
        if (data == null) {
            data = new ListPlayerDiscoveredAnchorSavedData();
            storage.set(ListPlayerDiscoveredAnchorSavedData.DATA_NAME, data); // Store the newly created data
        }

        return data;
    }

    /**
     * Saves the given chunk anchor data to the server's world storage, marking it as dirty.
     * This ensures the data will be saved during the next world save.
     *
     * @param level the ServerLevel instance where the data will be saved
     * @param data the ListChunkAnchorSavedData to be saved
     */
    public static void saveListChunkAnchorSavedData(ServerLevel level, ListChunkAnchorSavedData data) {
        DimensionDataStorage storage = level.getDataStorage();
        storage.set(ListChunkAnchorSavedData.DATA_NAME, data);
        data.setDirty();
    }

    /**
     * Saves the given player-discovered anchor data to the server's world storage, marking it as dirty.
     * This ensures the data will be saved during the next world save.
     *
     * @param level the ServerLevel instance where the data will be saved
     * @param data the ListPlayerDiscoveredAnchorSavedData to be saved
     */
    public static void saveListPlayerDiscoveredAnchorSavedData(ServerLevel level, ListPlayerDiscoveredAnchorSavedData data) {
        DimensionDataStorage storage = level.getDataStorage();
        storage.set(ListPlayerDiscoveredAnchorSavedData.DATA_NAME, data);
        data.setDirty();
    }
}