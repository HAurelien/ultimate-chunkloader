package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.ClientChunkUpdateHolder;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.GenericChunkUpdateHolder;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.ServerChunkUpdateHolder;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.slf4j.Logger;

import java.util.UUID;

public class ChunkUpdateHandler {
    private static ChunkUpdateHandler INSTANCE;
    private GenericChunkUpdateHolder HOLDER_INSTANCE;
    private ChunkLoader chunkLoader;
    private TicketHelper TICKET_HELPER;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Level currentLevel;

    public static ChunkUpdateHandler get(Level level) {
        if(INSTANCE == null){
            INSTANCE = new ChunkUpdateHandler(level);
        }
        currentLevel = level;
        return INSTANCE;
    }

    private ChunkUpdateHandler(Level level) {
        this.chunkLoader = ChunkLoader.get(level);
        if (level.isClientSide){
            if(HOLDER_INSTANCE == null){
                HOLDER_INSTANCE = ClientChunkUpdateHolder.get(level);
            }
        }
        else {
            if(HOLDER_INSTANCE == null){
                HOLDER_INSTANCE = ServerChunkUpdateHolder.get(level);
            }
        }
    }

    public void start(UUID playerId, ChunkPos chunkPos){
        try {
            chunkLoader.addChunkToLoad(chunkPos);
        }
        catch (Exception e){
            LOGGER.error("Error in ChunkLoader while trying to add a chunk : " + e.getLocalizedMessage());
        }
    }

    private void stop(UUID playerId, ChunkPos chunkPos){
        if(!currentLevel.isClientSide){
            try {
                chunkLoader.removeChunkToLoad(chunkPos);
            }catch (Exception e){
                LOGGER.error("Error in ChunkLoader while trying to remove a chunk : " + e.getLocalizedMessage());
            }
        }
    }

    public void addChunkMonitoring(UUID playerId, ChunkPos chunkPos){
        HOLDER_INSTANCE.addChunk(chunkPos, playerId);
        start(playerId, chunkPos);
    }

    public void removeChunkMonitoring(UUID playerId, ChunkPos chunkPos){
        HOLDER_INSTANCE.removeChunk(chunkPos, playerId);
        stop(playerId, chunkPos);
    }

    public void setTicketHelper(TicketHelper ticketHelper) {
        TICKET_HELPER = ticketHelper;
    }

    public void removeAllChunks(UUID playerId) {
        chunkLoader.CHUNKS_LOADED.forEach(chunk -> stop(playerId, chunk));
    }

    public boolean isChunkLoaded(ChunkPos currentChunkPos) {
        return chunkLoader.CHUNKS_LOADED.contains(currentChunkPos);
    }
}
