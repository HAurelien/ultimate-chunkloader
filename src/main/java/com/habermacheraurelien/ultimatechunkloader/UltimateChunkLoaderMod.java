package com.habermacheraurelien.ultimatechunkloader;

import com.habermacheraurelien.ultimatechunkloader.block.ModBlocks;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.ChunkAnchorHandler;
import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler.PlayerTracker;
import com.habermacheraurelien.ultimatechunkloader.component.ModDataComponents;
import com.habermacheraurelien.ultimatechunkloader.creativemodtab.ModCreativeModTabClass;
import com.habermacheraurelien.ultimatechunkloader.item.ModItems;
import com.habermacheraurelien.ultimatechunkloader.util.DataManager;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListPlayerDiscoveredAnchorSavedData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
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
        // Add Utils

        // Get the ticketHelper class
        // TODO: verify how relevant is the ResourceLocation
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
        // This is where you load your data when the server starts
        MinecraftServer server = event.getServer();

        // Example: Loading saved data
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();

        ListChunkAnchorSavedData chunkSavedData = dataStorage.computeIfAbsent(
                ListChunkAnchorSavedData.FACTORY,
                ListChunkAnchorSavedData.DATA_NAME
        );

        ListPlayerDiscoveredAnchorSavedData playerSavedData = dataStorage.computeIfAbsent(
                ListPlayerDiscoveredAnchorSavedData.FACTORY,
                ListPlayerDiscoveredAnchorSavedData.DATA_NAME
        );

        ChunkAnchorHandler.setChunkAnchorBlockArraySavedData(chunkSavedData);
        PlayerTracker.setListPlayerDiscoveredAnchorSavedData(playerSavedData);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event){
        MinecraftServer server = event.getServer();

        // Save data when the world is saved
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();

        // Get the save data of the specific classes
        ListChunkAnchorSavedData chunkAnchorData = DataManager.getListChunkAnchorSavedData(server);
        ListPlayerDiscoveredAnchorSavedData discoveredAnchorData = DataManager.getListPlayerDiscoveredAnchorSavedData(server);

        // Save the data back to DimensionDataStorage
        dataStorage.set(ListChunkAnchorSavedData.DATA_NAME, chunkAnchorData);  // Use the same key as when loading data
        dataStorage.set(ListPlayerDiscoveredAnchorSavedData.DATA_NAME, discoveredAnchorData);  // Use the same key as when loading data
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
