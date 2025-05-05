package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChunkAnchorHandler {
    private static ListChunkAnchorSavedData chunkAnchorBlockArraySavedData = null;

    public static void setChunkAnchorBlockArraySavedData(ListChunkAnchorSavedData savedData){
        chunkAnchorBlockArraySavedData = savedData;
        intiChunks();
    }

    private static void intiChunks(){
    }

    private static Integer getBlockId(BlockPos pos){
        Optional<ChunkAnchorBlockModel> chunkAnchorBlock = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList()
                .stream().filter(monitoredChunk -> monitoredChunk.isAtPos(pos)).findFirst();
        if(chunkAnchorBlock.isPresent()){
            return chunkAnchorBlock.get().getId();
        }
        return null;
    }

    private static ChunkAnchorBlockModel getBlockById(Integer id){
        return chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .filter(chunkAnchorBlockModel -> chunkAnchorBlockModel.getId() == id)
                .findFirst().orElseGet(null);
    }

    public static void registerAnchor(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if(level.isClientSide){
            return;
        }
        boolean exist = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .anyMatch(monitoredChunk -> monitoredChunk.isAtPos(pos));
        if(!exist){
            chunkAnchorBlockArraySavedData.addAnchor(new ChunkAnchorBlockModel(pos, level.dimension()));
        }
    }

    public static boolean canRemoveAnchor(BlockPos blockPos, Level level){
        if(level.isClientSide){
            return false;
        }
        return chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .noneMatch(chunkAnchor -> chunkAnchor.isAtPos(blockPos) && chunkAnchor.isActive());
    }

    public static void removeAnchor(BlockPos blockPos, ServerLevel level){
        if(level.isClientSide){
            return;
        }

        Optional<ChunkAnchorBlockModel> chunkAnchor = chunkAnchorBlockArraySavedData.getAnchor(blockPos);

        if(chunkAnchor.isPresent()){
            removeAnchorLoading(chunkAnchor.get(), level);
            PlayerTracker.removeBlockFromAllPlayers(chunkAnchor.get());
            chunkAnchorBlockArraySavedData.removeAnchor(chunkAnchor.get());
        }
    }

    public static void registerBlockToPlayer(BlockPos pos, Level level, UUID playerId){
        if(level.isClientSide){
           return;
        }
        Integer id = getBlockId(pos);
        if(id != null){
            PlayerTracker.addBlock(id, level, playerId);
        }
    }

    public static void changeAnchorStatus(MinecraftServer server, Integer anchorId) {
        Optional<ChunkAnchorBlockModel> anchor = chunkAnchorBlockArraySavedData.getAnchor(anchorId);
        if(anchor.isEmpty()){
            return;
        }
        Boolean isActive = chunkAnchorBlockArraySavedData.updateAnchorStatus(anchorId);
        ServerLevel level = server.getLevel(anchor.get().getDimension());
        if (isActive){
            addAnchorLoading(anchor.get(), level);
        }
        else {
            removeAnchorLoading(anchor.get(), level);
        }
    }

    public static void init(MinecraftServer server) {
        List<ChunkAnchorBlockModel> anchors = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList();

        for (ChunkAnchorBlockModel anchor : anchors) {
            if (!anchor.isActive()) continue;

            ServerLevel level = server.getLevel(anchor.getDimension());
            if (level == null) {
                continue;
            }

            addAnchorLoading(anchor, level);
        }
    }

    private static void removeAnchorLoading(ChunkAnchorBlockModel anchor, ServerLevel level){
        ChunkUpdateHandler.get(level).removeChunkMonitoring(new ChunkPos(anchor.getPos()));
    }

    private static void addAnchorLoading(ChunkAnchorBlockModel anchor, ServerLevel level){
        ChunkUpdateHandler.get(level).addChunkMonitoring(new ChunkPos(anchor.getPos()));
    }
}
