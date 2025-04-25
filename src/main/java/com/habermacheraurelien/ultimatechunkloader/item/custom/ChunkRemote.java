package com.habermacheraurelien.ultimatechunkloader.item.custom;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.block.ModBlocks;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.component.ModDataComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.UUID;

public class ChunkRemote extends Item {
    public static String ID = "chunk_remote";
    public static int MAX_STACK = 1;
    private static final String BASE_ADDRESS_TRANSLATABLE = "tooltip." + UltimateChunkLoaderMod.MOD_ID + "." + ID + ".";
    private static final String TOOLTIP_NO_COORDINATES = BASE_ADDRESS_TRANSLATABLE + "no_coordinates";
    private static final String TOOLTIP_WITH_COORDINATES = BASE_ADDRESS_TRANSLATABLE + "with_coordinates";
    private static final String TOOLTIP_ON = BASE_ADDRESS_TRANSLATABLE + "on";
    private static final String TOOLTIP_OFF = BASE_ADDRESS_TRANSLATABLE + "off";

    public ChunkRemote(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();
        if(clickedBlock == ModBlocks.CHUNK_ANCHOR.get()){

        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack remote = player.getItemInHand(usedHand);
        ChunkPos currentChunkPos = player.chunkPosition();
        if(Screen.hasShiftDown()){
            updateCoordinates(remote, currentChunkPos);
        } else {
            ChunkUpdateHandler chunkUpdateHandler = ChunkUpdateHandler.get(level);
            switchChunkState(player.getUUID(), currentChunkPos, chunkUpdateHandler);
            updateChunkState(remote, currentChunkPos, chunkUpdateHandler);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Integer X = stack.get(ModDataComponents.ANCHOR_COORDINATE_X);
        Integer Z = stack.get(ModDataComponents.ANCHOR_COORDINATE_Z);
        Boolean isChunkCurrentlyLoaded = stack.get(ModDataComponents.REMOTE_CHUNK_LOADED);
        tooltipComponents.add(Boolean.TRUE.equals(isChunkCurrentlyLoaded) ?
                Component.translatable(TOOLTIP_ON) : Component.translatable(TOOLTIP_OFF));

        if(X == null || Z == null){
            tooltipComponents.add(Component.translatable(TOOLTIP_NO_COORDINATES));
        }
        else {
            tooltipComponents.add(Component.translatable(TOOLTIP_WITH_COORDINATES, X, Z));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private void switchChunkState(UUID playerId, ChunkPos chunkPos, ChunkUpdateHandler chunkUpdateHandler){
        if(chunkUpdateHandler.isChunkLoaded(chunkPos)) {
            chunkUpdateHandler.removeChunkMonitoring(playerId, chunkPos);
        }
        else {
            chunkUpdateHandler.addChunkMonitoring(playerId, chunkPos);
        }
    }

    private void updateCoordinates(ItemStack remote, ChunkPos newChunkPos){
        remote.set(ModDataComponents.ANCHOR_COORDINATE_X, newChunkPos.x);
        remote.set(ModDataComponents.ANCHOR_COORDINATE_Z, newChunkPos.z);
    }

    private void updateChunkState(ItemStack remote, ChunkPos chunkPos, ChunkUpdateHandler chunkUpdateHandler){
        remote.set(ModDataComponents.REMOTE_CHUNK_LOADED, chunkUpdateHandler.isChunkLoaded(chunkPos));
    }
}
