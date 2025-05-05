package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler;

import com.habermacheraurelien.ultimatechunkloader.model.MonitoredChunkModel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.UUID;

public class ServerChunkUpdateHolder implements GenericChunkUpdateHolder {
    protected static ServerChunkUpdateHolder INSTANCE;
    protected static Level LEVEL;

    protected final ArrayList<MonitoredChunkModel> chunkList = new ArrayList<>();

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
        LEVEL = level;
    }

    @Override
    public void addChunk(ChunkPos chunkPos) {
        if(chunkList.stream().noneMatch(chunk -> chunk.hasPos(chunkPos))){
            chunkList.add(new MonitoredChunkModel(chunkPos));
        }
    }

    @Override
    public void addChunk(ChunkPos chunkPos, UUID playerId) {
        if(chunkList.stream().noneMatch(chunk -> chunk.hasPos(chunkPos))){
            chunkList.add(new MonitoredChunkModel(chunkPos, playerId));
        }
    }

    @Override
    public void removeChunk(ChunkPos chunkPos, UUID playerId) {
        chunkList.removeIf(chunk -> chunk.hasPos(chunkPos) && chunk.isPlayerResponsible(playerId));
    }

    @Override
    public void removeChunk(ChunkPos chunkPos) {
        chunkList.removeIf(chunk -> chunk.hasPos(chunkPos));
    }

    @Override @Nullable
    public UUID getChunkOwner(ChunkPos chunkPos) {
        return chunkList.stream().filter(chunk -> chunk.hasPos(chunkPos)).findFirst()
                .map(chunk -> chunk.playerIdResponsible).orElse(null);
    }

    @Override
    public boolean isChunkMonitored(ChunkPos chunkPos) {
        return chunkList.stream().anyMatch(chunk -> chunk.hasPos(chunkPos));
    }
}
