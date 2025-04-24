package com.habermacheraurelien.ultimatechunkloader.model;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

public class MonitoredChunk {
    public ChunkPos chunkPos;
    public Player playerResponsible;

    public MonitoredChunk(ChunkPos _chunkPos){
        chunkPos = _chunkPos;
    }
    public MonitoredChunk(ChunkPos _chunkPos, Player _playerResponsible){
        chunkPos = _chunkPos;
        playerResponsible = _playerResponsible;
    }

    public boolean hasPos(ChunkPos _chunkPos){
        return _chunkPos.x == chunkPos.x && _chunkPos.z == chunkPos.z;
    }

    public boolean isPlayerResponsible(Player player){
        return player.is(playerResponsible);
    }

}
