package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.ServerChunkUpdateHolder;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the monitoring and loading of chunks for the server-side dimension.
 * This class manages chunks that need to be loaded or monitored, and interacts
 * with the {@link ChunkLoader} and {@link ServerChunkUpdateHolder} for chunk operations.
 */
public class ChunkUpdateHandler {

    // Store instances of ChunkUpdateHandler for each dimension.
    private static final Map<ResourceKey<Level>, ChunkUpdateHandler> INSTANCES = new HashMap<>();

    // Instance of ServerChunkUpdateHolder that holds the chunks to be monitored.
    private final ServerChunkUpdateHolder holderInstance;

    // Instance of ChunkLoader used to load and remove chunks.
    private final ChunkLoader chunkLoader;

    // Logger to log errors during chunk handling.
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Retrieves an existing or new ChunkUpdateHandler for the specified level.
     * Ensures this handler is only used on the server side.
     *
     * @param level The {@link Level} for which to get the ChunkUpdateHandler.
     * @return The ChunkUpdateHandler for the specified level.
     * @throws IllegalStateException if accessed on the client side.
     */
    public static ChunkUpdateHandler get(Level level) {
        // Ensure the handler is only accessed on the server side
        if (level.isClientSide) {
            throw new IllegalStateException("ChunkUpdateHandler should only be accessed on the server side.");
        }

        // Create or retrieve the handler for the given dimension.
        return INSTANCES.computeIfAbsent(level.dimension(), dim -> new ChunkUpdateHandler(level));
    }

    /**
     * Private constructor to initialize the ChunkUpdateHandler for a specific level.
     *
     * @param level The {@link Level} in which chunks are being managed.
     */
    private ChunkUpdateHandler(Level level) {
        this.chunkLoader = ChunkLoader.get(level); // Get the chunk loader for this level.
        this.holderInstance = ServerChunkUpdateHolder.get(level); // Get the chunk update holder for this level.
    }

    /**
     * Adds a chunk to be monitored and loads it if necessary.
     *
     * @param chunkPos The {@link ChunkPos} representing the chunk to be monitored.
     */
    public void addChunkMonitoring(ChunkPos chunkPos) {
        // Add chunk to the monitoring holder instance.
        holderInstance.addChunk(chunkPos);
        try {
            // Attempt to load the chunk.
            chunkLoader.addChunkToLoad(chunkPos);
        } catch (Exception e) {
            // Log any error that occurs while adding the chunk.
            LOGGER.error("Error adding chunk: {}", e.getLocalizedMessage());
        }
    }

    /**
     * Removes a chunk from monitoring and unloads it if necessary.
     *
     * @param chunkPos The {@link ChunkPos} representing the chunk to be removed.
     */
    public void removeChunkMonitoring(ChunkPos chunkPos) {
        // Remove chunk from the monitoring holder instance.
        holderInstance.removeChunk(chunkPos);
        try {
            // Attempt to remove the chunk from loading.
            chunkLoader.removeChunkToLoad(chunkPos);
        } catch (Exception e) {
            // Log any error that occurs while removing the chunk.
            LOGGER.error("Error removing chunk: {}", e.getLocalizedMessage());
        }
    }

    /**
     * Removes all chunks from monitoring and unloading.
     * This method iterates over all currently loaded chunks and attempts to remove them.
     */
    public void removeAllChunks() {
        chunkLoader.CHUNKS_LOADED.forEach(chunk -> {
            try {
                // Attempt to remove each loaded chunk.
                chunkLoader.removeChunkToLoad(chunk);
            } catch (Exception e) {
                // Log any error that occurs while removing all chunks.
                LOGGER.error("Error removing all chunks for player: {}", e.getLocalizedMessage());
            }
        });
    }

    /**
     * Checks if a specific chunk is currently loaded.
     *
     * @param chunkPos The {@link ChunkPos} of the chunk to check.
     * @return {@code true} if the chunk is loaded, otherwise {@code false}.
     */
    public boolean isChunkLoaded(ChunkPos chunkPos) {
        // Return whether the chunk is in the loaded chunk set.
        return chunkLoader.CHUNKS_LOADED.contains(chunkPos);
    }

    /**
     * Placeholder method to set a TicketHelper. This method is not yet implemented.
     *
     * @param ticketHelper The {@link TicketHelper} to set.
     */
    public void setTicketHelper(TicketHelper ticketHelper) {
        // Currently, this method does nothing as the ticket handling logic is not implemented.
    }
}