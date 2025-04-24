package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler;

import com.habermacheraurelien.ultimatechunkloader.model.MonitoredChunk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class ServerChunkUpdateHolder implements GenericChunkUpdateHolder {
    protected static ServerChunkUpdateHolder INSTANCE;
    protected static Level LEVEL;

    protected final ArrayList<MonitoredChunk> chunkList = new ArrayList<>();

    public static ServerChunkUpdateHolder get(Level level) {
        if (level.isClientSide){
            throw new InvalidParameterException("Chunks can only be loaded server-side");
        }
        if(INSTANCE == null){
            INSTANCE = new ServerChunkUpdateHolder(level);
        }
        return INSTANCE;
    }

    private ServerChunkUpdateHolder(Level level){
        this.LEVEL = level;
    }

    @Override
    public void addChunk(ChunkPos chunkPos) {
        if(chunkList.stream().noneMatch(chunk -> chunk.hasPos(chunkPos))){
            chunkList.add(new MonitoredChunk(chunkPos));
        }
    }

    @Override
    public void addChunk(ChunkPos chunkPos, Player player) {
        if(chunkList.stream().noneMatch(chunk -> chunk.hasPos(chunkPos))){
            chunkList.add(new MonitoredChunk(chunkPos, player));
        }
    }

    @Override
    public void removeChunk(ChunkPos chunkPos, Player player) {
        chunkList.removeIf(chunk -> chunk.hasPos(chunkPos) && chunk.isPlayerResponsible(player));
    }

    @Override
    public void removeChunk(ChunkPos chunkPos) {
        chunkList.removeIf(chunk -> chunk.hasPos(chunkPos));
    }

    @Override
    public Player getChunkOwner(ChunkPos chunkPos) {
        return chunkList.stream().filter(chunk -> chunk.hasPos(chunkPos))
                .map(chunk -> chunk.playerResponsible).findFirst().orElse(null);
    }

    @Override
    public boolean isChunkMonitored(ChunkPos chunkPos) {
        return chunkList.stream().anyMatch(chunk -> chunk.hasPos(chunkPos));
    }
}
