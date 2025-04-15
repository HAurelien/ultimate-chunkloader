package com.habermacheraurelien.ultimatechunkloader.item.custom;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.component.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class ChunkRemote extends Item {
    public static String ID = "chunk_remote";
    public static int MAX_STACK = 1;
    private static final String BASE_ADDRESS_TRANSLATABLE = "tooltip." + UltimateChunkLoaderMod.MOD_ID + "." + ID + ".";
    private static final String TOOLTIP_NO_COORDINATES = BASE_ADDRESS_TRANSLATABLE + "no_coordinates";
    private static final String TOOLTIP_WITH_COORDINATES = BASE_ADDRESS_TRANSLATABLE + "with_coordinates";

    public ChunkRemote(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack remote = player.getItemInHand(usedHand);
        ChunkPos currentChunkPos = player.chunkPosition();
        if(Screen.hasShiftDown()){
            remote.set(ModDataComponents.COORDINATE_X, currentChunkPos.x);
            remote.set(ModDataComponents.COORDINATE_Z, currentChunkPos.z);
        } else {
            if(!level.isClientSide) {
                if(Boolean.TRUE.equals(remote.get(ModDataComponents.CHUNK_LOADED))){
                    ChunkUpdateHandler.get(level).stopChunkUpdateMonitoring(player, currentChunkPos);
                    remote.set(ModDataComponents.CHUNK_LOADED, false);
                    player.sendSystemMessage(Component.literal("Stopped monitoring chunk"));
                }
                else {
                    ChunkUpdateHandler.get(level).addChunkToMonitor(player, currentChunkPos);
                    remote.set(ModDataComponents.CHUNK_LOADED, true);
                    player.sendSystemMessage(Component.literal("Started monitoring chunk"));
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Integer X = stack.get(ModDataComponents.COORDINATE_X);
        Integer Z = stack.get(ModDataComponents.COORDINATE_Z);
        Boolean isChunkCurrentlyLoaded = stack.get(ModDataComponents.CHUNK_LOADED);
        String chunkState = Boolean.TRUE.equals(isChunkCurrentlyLoaded) ? "§aLoaded§r" : "§4Inactive§r";
        tooltipComponents.add(Component.literal("Chunk state : " + chunkState));

        if(X == null || Z == null){
            tooltipComponents.add(Component.translatable(TOOLTIP_NO_COORDINATES));
        }
        else {
            tooltipComponents.add(Component.translatable(TOOLTIP_WITH_COORDINATES, X, Z));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
