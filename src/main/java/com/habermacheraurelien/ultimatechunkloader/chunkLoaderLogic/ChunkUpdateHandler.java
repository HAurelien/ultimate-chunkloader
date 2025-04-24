package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.ClientChunkUpdateHolder;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.GenericChunkUpdateHolder;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler.ServerChunkUpdateHolder;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.slf4j.Logger;

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

    public void start(Player player, ChunkPos chunkPos){
        try {
            chunkLoader.addChunkToLoad(chunkPos);
        }
        catch (Exception e){
            LOGGER.error("Error in ChunkLoader while trying to add a chunk : " + e.getLocalizedMessage());
        }
    }

    private void stop(Player player, ChunkPos chunkPos){
        if(!currentLevel.isClientSide){
            try {
                chunkLoader.removeChunkToLoad(chunkPos);
            }catch (Exception e){
                LOGGER.error("Error in ChunkLoader while trying to remove a chunk : " + e.getLocalizedMessage());
            }
        }
    }

    public void addChunkMonitoring(Player player, ChunkPos chunkPos){
        HOLDER_INSTANCE.addChunk(chunkPos, player);
        start(player, chunkPos);
    }

    public void removeChunkMonitoring(Player player, ChunkPos chunkPos){
        HOLDER_INSTANCE.removeChunk(chunkPos, player);
        stop(player, chunkPos);
    }

    public void setTicketHelper(TicketHelper ticketHelper) {
        TICKET_HELPER = ticketHelper;
    }

    public void debugWithChunkHelper(Player player){
        player.sendSystemMessage(Component.literal("List of block tickets : " + TICKET_HELPER.getEntityTickets()));
        player.sendSystemMessage(Component.literal("List of entity tickets : " + TICKET_HELPER.getEntityTickets()));
    }

    public void removeAllChunks(Player player) {
        chunkLoader.CHUNKS_LOADED.forEach(chunk -> stop(player, chunk));
    }

    public boolean isChunkLoaded(ChunkPos currentChunkPos) {
        return chunkLoader.CHUNKS_LOADED.contains(currentChunkPos);
    }
}
