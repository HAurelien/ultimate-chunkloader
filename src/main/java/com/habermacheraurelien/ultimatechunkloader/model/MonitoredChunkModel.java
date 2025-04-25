package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

public class MonitoredChunkModel {
    public ChunkPos chunkPos;
    public UUID playerIdResponsible;

    public MonitoredChunkModel(ChunkPos _chunkPos){
        chunkPos = _chunkPos;
    }
    public MonitoredChunkModel(ChunkPos _chunkPos, UUID _playerResponsible){
        chunkPos = _chunkPos;
        playerIdResponsible = _playerResponsible;
    }

    public MonitoredChunkModel(Integer _x, Integer _y, String _playerIdResponsible) {
        chunkPos = new ChunkPos(_x, _y);
        playerIdResponsible = UUID.fromString(_playerIdResponsible);
    }

    public boolean hasPos(ChunkPos _chunkPos){
        return _chunkPos.x == chunkPos.x && _chunkPos.z == chunkPos.z;
    }

    public boolean isPlayerResponsible(UUID playerId){
        return playerId.equals(playerIdResponsible);
    }

    private int getPosX(){
        return chunkPos.x;
    }

    private int getPosZ(){
        return chunkPos.z;
    }

    private String getUUIDAsString(){
        return playerIdResponsible.toString();
    }

    public static final Codec<MonitoredChunkModel> chunkAnchorBlockModelCodec = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(MonitoredChunkModel::getPosX),
                    Codec.INT.fieldOf("Z").forGetter(MonitoredChunkModel::getPosZ),
                    Codec.STRING.fieldOf("uuid").forGetter(MonitoredChunkModel::getUUIDAsString)
            ).apply(instance, MonitoredChunkModel::new));
}
