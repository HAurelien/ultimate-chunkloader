package com.habermacheraurelien.ultimatechunkloader.item;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.item.custom.ChunkRemote;
import com.habermacheraurelien.ultimatechunkloader.item.custom.DebugStick;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UltimateChunkLoaderMod.MOD_ID);

    public static final DeferredItem<ChunkRemote> CHUNK_REMOTE = ITEMS.register(ChunkRemote.ID,
            () -> new ChunkRemote(new Item.Properties().stacksTo(ChunkRemote.MAX_STACK)));

    public static final DeferredItem<DebugStick> DEBUG_STICK = ITEMS.register(DebugStick.ID,
            () -> new DebugStick(new Item.Properties().stacksTo(DebugStick.MAX_STACK)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
