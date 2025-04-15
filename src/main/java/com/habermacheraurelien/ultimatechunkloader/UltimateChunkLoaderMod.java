package com.habermacheraurelien.ultimatechunkloader;

import com.habermacheraurelien.ultimatechunkloader.block.ModBlocks;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkLoader;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.component.ModDataComponents;
import com.habermacheraurelien.ultimatechunkloader.creativemodtab.ModCreativeModTabClass;
import com.habermacheraurelien.ultimatechunkloader.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(UltimateChunkLoaderMod.MOD_ID)
public class UltimateChunkLoaderMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ultimatechunkloader";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Ticket controller
    public static final TicketController TICKET_CONTROLLER;

    static {
        TICKET_CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath("chunkloaders", "chunks"),
                ((level, ticketHelper) -> {
                    ChunkUpdateHandler.get(level).setTicketHelper(ticketHelper);
                })
        );
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public UltimateChunkLoaderMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the controller for chunk loading
        modEventBus.addListener(this::registerTicketController);

        // Register ourselves for server and other game events we are interested in.
        NeoForge.EVENT_BUS.register(this);

        // Register the T from the ModT classes
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModCreativeModTabClass.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //TODO: load list of updated chunks
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @SubscribeEvent
    public void onWorldSave(LevelEvent.Save e) {
        //TODO : Save the loaded chunks
    }

    /**
     * This function registers the controller we will use in order to store in Minecraft the automatically updated chunks
     *
     * @param e The event necessary to register the controller
     */
    private void registerTicketController(RegisterTicketControllersEvent e){
        e.register(TICKET_CONTROLLER);
    }
}
