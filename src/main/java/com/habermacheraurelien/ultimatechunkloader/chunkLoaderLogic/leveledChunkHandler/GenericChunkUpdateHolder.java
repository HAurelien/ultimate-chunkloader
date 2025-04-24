package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler;

import com.habermacheraurelien.ultimatechunkloader.model.MonitoredChunk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;

/**
 * This is only supposed to keep track of the chunks currently activated.
 */
public interface GenericChunkUpdateHolder {
    /**
     * Monitors a new chunk.
     * @param chunkPos
     */
    public void addChunk(ChunkPos chunkPos);
    /**
     * Monitors a new chunk. Handle player ownership.
     * @param chunkPos Position of the chunk to monitore
     * @param player Player responsible for the activation of the chunk
     */
    public void addChunk(ChunkPos chunkPos, Player player);

    /**
     * Remove the chunk from the list of the monitored chunks. Handle player ownership.
     * @param chunkPos The position of the chunk
     * @param player The player who requested the chunk to stop being monitored
     */
    public void removeChunk(ChunkPos chunkPos, Player player);

    /**
     * Remove the chunk from the list of the monitored chunks.
     * @param chunkPos The position of the chunk
     */
    public void removeChunk(ChunkPos chunkPos);

    /**
     * Allow to know who is responsible for the chunk to be monitored
     * @param chunkPos
     * @return
     */
    public Player getChunkOwner(ChunkPos chunkPos);

    /**
     * Allow to know if the chunk is monitored
     * @param chunkPos
     * @return
     */
    public boolean isChunkMonitored(ChunkPos chunkPos);
}
