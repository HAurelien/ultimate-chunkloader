package com.habermacheraurelien.ultimatechunkloader.block.custom;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.ChunkAnchorHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class ChunkAnchorBlock extends Block {
    public static final String ID = "chunk_anchor";

    public ChunkAnchorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                                        BlockHitResult hitResult) {
        if(!level.isClientSide){
            ChunkAnchorHandler.registerBlockToPlayer(pos, level, player.getUUID());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        ChunkAnchorHandler.registerAnchor(state, level, pos, oldState);
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        // TODO: add blockstate when anchor active to know if can be destroyed from client
        if(!ChunkAnchorHandler.canRemoveAnchor(pos, level) || level.isClientSide){
            return false;
        }
        ChunkAnchorHandler.removeAnchor(pos, (ServerLevel) level);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void onDestroyedByPushReaction(BlockState state, Level level, BlockPos pos, Direction pushDirection, FluidState fluid) {
        // TODO: add blockstate when anchor active to know if can be destroyed from client
        if(!ChunkAnchorHandler.canRemoveAnchor(pos, level) || level.isClientSide){
            return;
        }
        ChunkAnchorHandler.removeAnchor(pos,(ServerLevel) level);
        super.onDestroyedByPushReaction(state, level, pos, pushDirection, fluid);
    }
}
