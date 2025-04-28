package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChunkAnchorHandler {
    private static ListChunkAnchorSavedData chunkAnchorBlockArraySavedData = null;
    //private static final ArrayList<ChunkAnchorBlockModel> chunkAnchorBlockArrayList = new ArrayList<ChunkAnchorBlockModel>();

    public static void setChunkAnchorBlockArraySavedData(ListChunkAnchorSavedData savedData){
        chunkAnchorBlockArraySavedData = savedData;
    }

    private static Integer getBlockId(BlockPos pos){
        Optional<ChunkAnchorBlockModel> chunkAnchorBlock = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList()
                .stream().filter(monitoredChunk -> monitoredChunk.isAtPos(pos)).findFirst();
        if(chunkAnchorBlock.isPresent()){
            return chunkAnchorBlock.get().getId();
        }
        //Optional<ChunkAnchorBlockModel> chunkAnchorBlock = chunkAnchorBlockArrayList.stream()
        //        .filter(monitoredChunk -> monitoredChunk.isAtPos(pos)).findFirst();
        //if(chunkAnchorBlock.isPresent()){
        //    return chunkAnchorBlock.get().getId();
        //}
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
        Player player = Minecraft.getInstance().player;
        if(!exist && player != null){
            chunkAnchorBlockArraySavedData.addAnchor(new ChunkAnchorBlockModel(pos, level.dimension().location().getPath()));
        }
    }

    public static boolean canRemoveAnchor(BlockPos blockPos, Level level){
        if(level.isClientSide){
            return false;
        }
        return !chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .anyMatch(chunkAnchor -> chunkAnchor.isAtPos(blockPos) && chunkAnchor.isActive());
    }

    public static void removeAnchor(BlockPos blockPos, Level level){
        if(level.isClientSide){
            return;
        }

        Optional<ChunkAnchorBlockModel> chunkAnchor = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList()
                .stream().filter(chunkAnchorBlockModel ->
                chunkAnchorBlockModel.isAtPos(blockPos) && !chunkAnchorBlockModel.isActive()).findFirst();

        chunkAnchor.ifPresent(chunkAnchorBlockModel ->
                chunkAnchorBlockArraySavedData.removeAnchor(chunkAnchorBlockModel));
    }

    public static ChunkAnchorBlockModel getAnchor(BlockPos pos, Level level) {
        if(level.isClientSide){
            return null;
        }
        Optional<ChunkAnchorBlockModel> block = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .filter(chunkAnchorBlockModel -> chunkAnchorBlockModel.isAtPos(pos))
                .findFirst();
        return block.orElse(null);
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

    public static List<ChunkAnchorBlockModel> getAllAnchorsKnownByPlayer(Level level, UUID playerId){
        if(level.isClientSide){
            return null;
        }
        PlayerAnchorTrackerModel playerAnchorTrackerModel = PlayerTracker
                .getAllIdsDiscoveredByPlayer(level, playerId).orElseGet(null);
        if(playerAnchorTrackerModel == null){
            return null;
        }
        return playerAnchorTrackerModel.getIdList().stream().map(ChunkAnchorHandler::getBlockById).toList();
    }
}
