package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler;

import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

/**
 * This is only supposed to keep track of the chunks currently activated.
 */
public interface GenericChunkUpdateHolder {
    /**
     * Monitors a new chunk.
     * @param chunkPos
     */
    void addChunk(ChunkPos chunkPos);
    /**
     * Monitors a new chunk. Handle player ownership.
     * @param chunkPos Position of the chunk to monitore
     * @param playerId UUID responsible for the activation of the chunk
     */
    void addChunk(ChunkPos chunkPos, UUID playerId);

    /**
     * Remove the chunk from the list of the monitored chunks. Handle player ownership.
     * @param chunkPos The position of the chunk
     * @param playerId The player who requested the chunk to stop being monitored
     */
    void removeChunk(ChunkPos chunkPos, UUID playerId);

    /**
     * Remove the chunk from the list of the monitored chunks.
     * @param chunkPos The position of the chunk
     */
    void removeChunk(ChunkPos chunkPos);

    /**
     * Allow to know who is responsible for the chunk to be monitored
     * @param chunkPos
     * @return
     */
    UUID getChunkOwner(ChunkPos chunkPos);

    /**
     * Allow to know if the chunk is monitored
     * @param chunkPos
     * @return
     */
    boolean isChunkMonitored(ChunkPos chunkPos);
}
