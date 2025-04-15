package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * The class handling the loading and maintaining the information about the currently loaded chunks.
 */
public class ChunkLoader {
    // UUID used to dissociate this particular mod chunk loader in case there are others
    private static final UUID GLOBAL_CHUNK_UUID = UUID.fromString("0c47e8fb-ea5d-4d3f-8286-b7a0d4be685c");

    protected final Level LEVEL;
    protected final ArrayList<ChunkPos> CHUNKS_LOADED = new ArrayList<>();
    protected static ChunkLoader INSTANCE;

    public static ChunkLoader get(Level level) {
        if (level.isClientSide){
            throw new InvalidParameterException("Chunks can only be loaded server-side");
        }
        if(INSTANCE == null){
            INSTANCE = new ChunkLoader(level);
        }
        return INSTANCE;
    }

    private ChunkLoader(Level level) {
        this.LEVEL = level;
    }

    public void addChunkToLoad(ChunkPos chunkPos) throws Exception {
        if(!CHUNKS_LOADED.contains(chunkPos)){
            CHUNKS_LOADED.add(chunkPos);
            UltimateChunkLoaderMod.TICKET_CONTROLLER.forceChunk((ServerLevel) this.LEVEL, GLOBAL_CHUNK_UUID,
                    chunkPos.x, chunkPos.z, true, true);
        }
    }

    public void removeChunkToLoad(ChunkPos chunkPos) throws Exception {
        if(CHUNKS_LOADED.contains(chunkPos)){
            UltimateChunkLoaderMod.TICKET_CONTROLLER.forceChunk((ServerLevel) this.LEVEL, GLOBAL_CHUNK_UUID,
                    chunkPos.x, chunkPos.z, false, false);
            CHUNKS_LOADED.remove(chunkPos);
        }
    }
}
