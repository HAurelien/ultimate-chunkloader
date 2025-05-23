package com.habermacheraurelien.ultimatechunkloader.item.custom;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.component.ModDataComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DebugStick extends Item {
    public static final String ID = "debug_stick";
    public static final int MAX_STACK = 1;

    private static final int MAX_MODE = 3;

    private static final String BASE_ADDRESS_TRANSLATABLE = "tooltip." + UltimateChunkLoaderMod.MOD_ID + "." + ID + ".";
    private static final String DESCRIPTION_GENERAL = BASE_ADDRESS_TRANSLATABLE + "general";
    private static final String DESCRIPTION_MODE = BASE_ADDRESS_TRANSLATABLE + "mode";
    private static final String BASE_MESSAGE_MODE = "message." + UltimateChunkLoaderMod.MOD_ID + "." + ID + ".";
    private static final String MESSAGE_MODE_0 = BASE_MESSAGE_MODE + "message_mode_0";
    private static final String MESSAGE_MODE_2 = BASE_MESSAGE_MODE + "message_mode_2";


    public DebugStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack debugStick = player.getItemInHand(usedHand);
        Integer debugMode = debugStick.get(ModDataComponents.DEBUG_STICK_MODE);
        if(debugMode == null){
            debugMode = -1;
        }
        if(Screen.hasShiftDown()){
            debugMode = (debugMode + 1) % MAX_MODE;
            debugStick.set(ModDataComponents.DEBUG_STICK_MODE, debugMode);
        } else {
            if(!level.isClientSide) {
                switch (debugMode){
                    case 0:
                        player.sendSystemMessage(Component.translatable(MESSAGE_MODE_0));
                    case 1:
                        player.sendSystemMessage(Component.literal("Forget it"));
                    case 2:
                        ChunkUpdateHandler.get(level).removeAllChunks();
                        player.sendSystemMessage(Component.translatable(MESSAGE_MODE_2));
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Integer debugMode = stack.get(ModDataComponents.DEBUG_STICK_MODE);
        if(debugMode != null && debugMode > 0){
            tooltipComponents.add(Component.translatable(DESCRIPTION_MODE + "_" + debugMode));
        }
        else {
            tooltipComponents.add(Component.translatable(DESCRIPTION_GENERAL, UltimateChunkLoaderMod.MOD_ID));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
