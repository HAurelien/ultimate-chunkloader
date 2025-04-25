package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.leveledChunkHandler;

import com.habermacheraurelien.ultimatechunkloader.model.MonitoredChunkModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.UUID;

public class ClientChunkUpdateHolder implements GenericChunkUpdateHolder {
    protected static ClientChunkUpdateHolder INSTANCE;
    protected static Level LEVEL;

    protected final ArrayList<MonitoredChunkModel> chunkList = new ArrayList<>();

    public static GenericChunkUpdateHolder get(Level level) {
        if (!level.isClientSide){
            level.dimensionType();
            throw new InvalidParameterException("Client-side holder called from server");
        }
        if(INSTANCE == null){
            INSTANCE = new ClientChunkUpdateHolder(level);
        }
        return INSTANCE;
    }

    private ClientChunkUpdateHolder(Level level){
        this.LEVEL = level;
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

    @Override
    public UUID getChunkOwner(ChunkPos chunkPos) {
        return chunkList.stream().filter(chunk -> chunk.hasPos(chunkPos))
                .map(chunk -> chunk.playerIdResponsible).findFirst().orElse(null);
    }

    @Override
    public boolean isChunkMonitored(ChunkPos chunkPos) {
        return chunkList.stream().anyMatch(chunk -> chunk.hasPos(chunkPos));
    }
}
