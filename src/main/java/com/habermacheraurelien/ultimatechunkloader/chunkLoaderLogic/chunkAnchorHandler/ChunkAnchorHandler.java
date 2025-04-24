package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockEntity;
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

public class ChunkAnchorHandler {
    private static final ArrayList<ChunkAnchorBlockEntity> chunkAnchorBlockArrayList = new ArrayList<ChunkAnchorBlockEntity>();

    private static Integer getBlockId(BlockPos pos){
        Optional<ChunkAnchorBlockEntity> chunkAnchorBlock = chunkAnchorBlockArrayList.stream()
                .filter(monitoredChunk -> monitoredChunk.isAtPos(pos)).findFirst();
        if(chunkAnchorBlock.isPresent()){
            return chunkAnchorBlock.get().getId();
        }
        return null;
    }

    private static ChunkAnchorBlockEntity getBlockById(Integer id){
        return chunkAnchorBlockArrayList.stream()
                .filter(chunkAnchorBlockEntity -> chunkAnchorBlockEntity.getId() == id)
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
            chunkAnchorBlockArrayList.add(new ChunkAnchorBlockEntity(pos, level.dimensionType()));
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
        chunkAnchorBlockArrayList.removeIf(chunkAnchorBlockEntity ->
                chunkAnchorBlockEntity.isAtPos(blockPos) && !chunkAnchorBlockEntity.isActive());
    }

    public static ChunkAnchorBlockEntity getAnchor(BlockPos pos, Level level) {
        if(level.isClientSide){
            return null;
        }
        Optional<ChunkAnchorBlockEntity> block = chunkAnchorBlockArrayList.stream()
                .filter(chunkAnchorBlockEntity -> chunkAnchorBlockEntity.isAtPos(pos))
                .findFirst();
        return block.orElse(null);
    }

    public static void registerBlockToPlayer(BlockPos pos, Level level, Player player){
        if(level.isClientSide){
           return;
        }
        Integer id = getBlockId(pos);
        if(id != null){
            PlayerTracker.addBlock(id, level, player);
        }
    }

    public static List<ChunkAnchorBlockEntity> getAllAnchorsKnownByPlayer(Level level, Player player){
        if(level.isClientSide){
            return null;
        }
        PlayerAnchorTrackerModel playerAnchorTrackerModel = PlayerTracker
                .getAllIdsDiscoveredByPlayer(level, player).orElseGet(null);
        if(playerAnchorTrackerModel == null){
            return null;
        }
        return playerAnchorTrackerModel.getIdList().stream().map(ChunkAnchorHandler::getBlockById).toList();
    }
}
