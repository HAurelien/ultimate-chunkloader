package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChunkAnchorHandler {
    private static final ArrayList<ChunkAnchorBlockModel> chunkAnchorBlockArrayList = new ArrayList<ChunkAnchorBlockModel>();

    private static Integer getBlockId(BlockPos pos){
        Optional<ChunkAnchorBlockModel> chunkAnchorBlock = chunkAnchorBlockArrayList.stream()
                .filter(monitoredChunk -> monitoredChunk.isAtPos(pos)).findFirst();
        if(chunkAnchorBlock.isPresent()){
            return chunkAnchorBlock.get().getId();
        }
        return null;
    }

    private static ChunkAnchorBlockModel getBlockById(Integer id){
        return chunkAnchorBlockArrayList.stream()
                .filter(chunkAnchorBlockModel -> chunkAnchorBlockModel.getId() == id)
                .findFirst().orElseGet(null);
    }

    public static void registerAnchor(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if(level.isClientSide){
            return;
        }
        boolean exist = chunkAnchorBlockArrayList.stream()
                .anyMatch(monitoredChunk -> monitoredChunk.isAtPos(pos));
        Player player = Minecraft.getInstance().player;
        if(!exist && player != null){
            player.sendSystemMessage(Component.literal("Anchor added to the list !"));
            chunkAnchorBlockArrayList.add(new ChunkAnchorBlockModel(pos, level.dimension().location().getPath()));
        }
    }

    public static boolean canRemoveAnchor(BlockPos blockPos, Level level){
        if(level.isClientSide){
            return false;
        }
        return !chunkAnchorBlockArrayList.stream()
                .anyMatch(chunkAnchor -> chunkAnchor.isAtPos(blockPos) && chunkAnchor.isActive());
    }

    public static void removeAnchor(BlockPos blockPos, Level level){
        if(level.isClientSide){
            return;
        }
        chunkAnchorBlockArrayList.removeIf(chunkAnchorBlockModel ->
                chunkAnchorBlockModel.isAtPos(blockPos) && !chunkAnchorBlockModel.isActive());
    }

    public static ChunkAnchorBlockModel getAnchor(BlockPos pos, Level level) {
        if(level.isClientSide){
            return null;
        }
        Optional<ChunkAnchorBlockModel> block = chunkAnchorBlockArrayList.stream()
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
