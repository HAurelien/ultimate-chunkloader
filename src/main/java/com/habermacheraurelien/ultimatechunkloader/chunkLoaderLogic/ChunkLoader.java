package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * Manages the loading and unloading of chunks on the server side. It keeps track of the chunks
 * that need to be loaded and communicates with the ticket controller to force chunk loading/unloading.
 */
public class ChunkLoader {

    // A global UUID used for all chunk loading operations.
    private static final UUID GLOBAL_CHUNK_UUID = UUID.fromString("0c47e8fb-ea5d-4d3f-8286-b7a0d4be685c");

    // A map to store instances of ChunkLoader for each dimension.
    private static final Map<ResourceKey<Level>, ChunkLoader> INSTANCES = new HashMap<>();

    // The Level (dimension) for which this ChunkLoader is responsible.
    protected final Level LEVEL;

    // List to keep track of chunks that are currently loaded.
    protected final ArrayList<ChunkPos> CHUNKS_LOADED = new ArrayList<>();

    /**
     * Retrieves an existing or new ChunkLoader for the specified level (dimension).
     * Ensures that ChunkLoader is only used on the server side.
     *
     * @param level The {@link Level} (dimension) for which to get the ChunkLoader.
     * @return The ChunkLoader instance for the given level.
     * @throws InvalidParameterException if accessed on the client side.
     */
    public static ChunkLoader get(Level level) {
        // Ensure ChunkLoader is only accessed on the server side.
        if (level.isClientSide) {
            throw new InvalidParameterException("Chunks can only be loaded server-side");
        }

        // Create or retrieve the ChunkLoader for the specified dimension.
        return INSTANCES.computeIfAbsent(level.dimension(), dim -> new ChunkLoader(level));
    }

    /**
     * Private constructor to initialize the ChunkLoader for a specific level (dimension).
     *
     * @param level The {@link Level} (dimension) to load chunks for.
     */
    private ChunkLoader(Level level) {
        this.LEVEL = level;
    }

    /**
     * Adds a chunk to the list of chunks that need to be loaded, and forces chunk loading for the given chunk position.
     *
     * @param chunkPos The {@link ChunkPos} representing the chunk to load.
     */
    public void addChunkToLoad(ChunkPos chunkPos) {
        // Only add the chunk if it isn't already loaded.
        if (!CHUNKS_LOADED.contains(chunkPos)) {
            CHUNKS_LOADED.add(chunkPos);
            // Force the chunk to load by using the ticket controller.
            UltimateChunkLoaderMod.TICKET_CONTROLLER.forceChunk((ServerLevel) this.LEVEL, GLOBAL_CHUNK_UUID,
                    chunkPos.x, chunkPos.z, true, true);
        }
    }

    /**
     * Removes a chunk from the list of chunks to be loaded, and forces chunk unloading for the given chunk position.
     *
     * @param chunkPos The {@link ChunkPos} representing the chunk to unload.
     */
    public void removeChunkToLoad(ChunkPos chunkPos) {
        // Only remove the chunk if it is currently loaded.
        if (CHUNKS_LOADED.contains(chunkPos)) {
            // Force the chunk to unload using the ticket controller.
            UltimateChunkLoaderMod.TICKET_CONTROLLER.forceChunk((ServerLevel) this.LEVEL, GLOBAL_CHUNK_UUID,
                    chunkPos.x, chunkPos.z, false, false);
            // Remove the chunk from the loaded chunk list.
            CHUNKS_LOADED.remove(chunkPos);
        }
    }
}