package com.habermacheraurelien.ultimatechunkloader.util;

import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class DataManager {

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

    public static ListPlayerDiscoveredAnchorSavedData getListPlayerDiscoveredAnchorSavedData(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();

        // Try to load existing data (returns null if not found)
        ListPlayerDiscoveredAnchorSavedData data = (ListPlayerDiscoveredAnchorSavedData) storage.get(ListPlayerDiscoveredAnchorSavedData.FACTORY, ListPlayerDiscoveredAnchorSavedData.DATA_NAME);

        // If the data is not found, create new instance and save it
        if (data == null) {
            data = new ListPlayerDiscoveredAnchorSavedData();
            storage.set(ListPlayerDiscoveredAnchorSavedData.DATA_NAME, data); // Store the newly created data
        }

        return data;
    }

    public static void saveListChunkAnchorSavedData(ServerLevel level, ListChunkAnchorSavedData data) {
        // Mark the data as dirty, ensuring it gets saved during world save
        DimensionDataStorage storage = level.getDataStorage();
        storage.set(ListChunkAnchorSavedData.DATA_NAME, data);
        data.setDirty(); // This tells Minecraft to save the data on the next world save
    }

    public static void saveListPlayerDiscoveredAnchorSavedData(ServerLevel level, ListPlayerDiscoveredAnchorSavedData data) {
        // Mark the data as dirty, ensuring it gets saved during world save
        DimensionDataStorage storage = level.getDataStorage();
        storage.set(ListPlayerDiscoveredAnchorSavedData.DATA_NAME, data);
        data.setDirty(); // This tells Minecraft to save the data on the next world save
    }
}