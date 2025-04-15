package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.slf4j.Logger;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class ChunkUpdateHandler {
    private static ChunkUpdateHandler INSTANCE;
    private Level LEVEL;
    private ChunkLoader chunkLoader;
    private TicketHelper TICKET_HELPER;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ChunkUpdateHandler get(Level level) {
        if (level.isClientSide){
            throw new InvalidParameterException("Chunks can only be loaded server-side");
        }
        if(INSTANCE == null){
            INSTANCE = new ChunkUpdateHandler(level);
        }
        return INSTANCE;
    }

    private ChunkUpdateHandler(Level level) {
        this.LEVEL = level;
        this.chunkLoader = ChunkLoader.get(level);
    }

    public void addChunkToMonitor(Player player, ChunkPos chunkPos){
        try {
            chunkLoader.addChunkToLoad(chunkPos);
        }
        catch (Exception e){
            LOGGER.error("Error in ChunkLoader while trying to add a chunk : " + e.getLocalizedMessage());
        }
    }

    public void updateChunkToMonitor(Player player, ChunkPos chunkPos){
    }

    public void stopChunkUpdateMonitoring(Player player, ChunkPos chunkPos){
        try {
            chunkLoader.removeChunkToLoad(chunkPos);
        }catch (Exception e){
            LOGGER.error("Error in ChunkLoader while trying to remove a chunk : " + e.getLocalizedMessage());
        }
    }

    public void setTicketHelper(TicketHelper ticketHelper) {
        TICKET_HELPER = ticketHelper;
    }

    public void debugWithChunkHelper(Player player){
        player.sendSystemMessage(Component.literal("List of block tickets : " + TICKET_HELPER.getEntityTickets()));
        player.sendSystemMessage(Component.literal("List of entity tickets : " + TICKET_HELPER.getEntityTickets()));
    }

    public void removeAllChunks(Player player) {
        chunkLoader.CHUNKS_LOADED.forEach(chunk -> stopChunkUpdateMonitoring(player, chunk));
    }
}
