package com.habermacheraurelien.ultimatechunkloader.item.custom;

import com.habermacheraurelien.ultimatechunkloader.GUI.screens.AnchorListScreen;
import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.component.ModDataComponents;
import com.habermacheraurelien.ultimatechunkloader.util.networking.payloads.RequestForScreenAnchorTrackerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * ChunkRemote is an item that allows players to interact with the chunk anchors on the server.
 * When used, it sends a request to the server for the player's screen anchor tracker data
 * and then opens the AnchorListScreen on the client side.
 */
public class ChunkRemote extends Item {

    // The unique ID for this item, used for registration.
    public static final String ID = "chunk_remote";

    // The maximum stack size for this item (set to 1 as this item is meant to be used individually).
    public static final int MAX_STACK = 1;

    /**
     * Constructor for the ChunkRemote item. It passes item properties to the super class (Item).
     *
     * @param properties The properties for the item (such as its default name, tab, etc.).
     */
    public ChunkRemote(Properties properties) {
        super(properties);
    }

    /**
     * Called when the player uses the ChunkRemote item. It sends a request to the server to fetch
     * the player's screen anchor tracker data and opens the {@link AnchorListScreen} on the client side.
     *
     * @param level The current world level where the player is.
     * @param player The player who is using the ChunkRemote.
     * @param usedHand The hand in which the player used the item (either MAIN_HAND or OFF_HAND).
     *
     * @return An {@link InteractionResultHolder} representing the outcome of the item usage.
     *         In this case, it always returns success with the item stack in the hand.
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        // Client-side actions for interacting with the chunk remote
        if(level.isClientSide) {
            // Send request to the server to get the player's screen anchor tracker data
            PacketDistributor.sendToServer(new RequestForScreenAnchorTrackerModel(player.getUUID().toString()));

            // Open the AnchorListScreen to display chunk anchors
            AnchorListScreen anchorScreen = new AnchorListScreen();
            Minecraft.getInstance().setScreen(anchorScreen);
        }

        // Return success and allow the item to be used without changing the item in hand
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
