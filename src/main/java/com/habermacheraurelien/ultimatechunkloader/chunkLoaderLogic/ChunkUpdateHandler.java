package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.ServerChunkUpdateHolder;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ChunkUpdateHandler {
    private static final Map<ResourceKey<Level>, ChunkUpdateHandler> INSTANCES = new HashMap<>();
    private final ServerChunkUpdateHolder holderInstance;
    private final ChunkLoader chunkLoader;
    private TicketHelper ticketHelper;
    private final Level level;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ChunkUpdateHandler get(Level level) {
        if (level.isClientSide) {
            throw new IllegalStateException("ChunkUpdateHandler should only be accessed on the server side.");
        }
        return INSTANCES.computeIfAbsent(level.dimension(), dim -> new ChunkUpdateHandler(level));
    }

    private ChunkUpdateHandler(Level level) {
        this.level = level;
        this.chunkLoader = ChunkLoader.get(level);
        this.holderInstance = ServerChunkUpdateHolder.get(level);
    }

    public void addChunkMonitoring(ChunkPos chunkPos) {
        holderInstance.addChunk(chunkPos);
        try {
            chunkLoader.addChunkToLoad(chunkPos);
        } catch (Exception e) {
            LOGGER.error("Error adding chunk: " + e.getLocalizedMessage());
        }
    }

    public void removeChunkMonitoring(ChunkPos chunkPos) {
        holderInstance.removeChunk(chunkPos);
        try {
            chunkLoader.removeChunkToLoad(chunkPos);
        } catch (Exception e) {
            LOGGER.error("Error removing chunk: " + e.getLocalizedMessage());
        }
    }

    public void removeAllChunks() {
        chunkLoader.CHUNKS_LOADED.forEach(chunk -> {
            try {
                chunkLoader.removeChunkToLoad(chunk);
            } catch (Exception e) {
                LOGGER.error("Error removing all chunks for player: " + e.getLocalizedMessage());
            }
        });
    }

    public boolean isChunkLoaded(ChunkPos chunkPos) {
        return chunkLoader.CHUNKS_LOADED.contains(chunkPos);
    }

    public void setTicketHelper(TicketHelper ticketHelper) {
        this.ticketHelper = ticketHelper;
    }
}
