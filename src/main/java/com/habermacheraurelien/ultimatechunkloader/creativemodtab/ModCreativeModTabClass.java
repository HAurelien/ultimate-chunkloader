package com.habermacheraurelien.ultimatechunkloader.creativemodtab;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabClass {
    private static final String BASE_TAB_TITLE = "creativetab." + UltimateChunkLoaderMod.MOD_ID + ".";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UltimateChunkLoaderMod.MOD_ID);

    private static final String COMMAND_TAB_ID =  UltimateChunkLoaderMod.MOD_ID + "_command_tab";
    private static final String COMMAND_TAB_TITLE = BASE_TAB_TITLE + "command_tab";

    public static final Supplier<CreativeModeTab> COMMAND_TAB = CREATIVE_MODE_TAB.register(
            COMMAND_TAB_ID, () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.CHUNK_REMOTE.get()))
                    .title(Component.translatable(COMMAND_TAB_TITLE))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CHUNK_REMOTE.asItem());
                        output.accept(ModItems.DEBUG_STICK.asItem());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
