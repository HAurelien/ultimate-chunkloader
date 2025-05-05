package com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.chunkAnchorHandler;

import com.habermacheraurelien.ultimatechunkloader.chunkLoaderLogic.ChunkUpdateHandler;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.habermacheraurelien.ultimatechunkloader.util.save.ListChunkAnchorSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles operations related to chunk anchors, such as registering anchors,
 * changing their status, adding/removing them, and managing associated chunk loading.
 */
public class ChunkAnchorHandler {

    // The saved data for chunk anchor blocks.
    private static ListChunkAnchorSavedData chunkAnchorBlockArraySavedData = null;

    /**
     * Sets the saved chunk anchor block data and initializes the chunks.
     *
     * @param savedData The saved chunk anchor block data.
     */
    public static void setChunkAnchorBlockArraySavedData(ListChunkAnchorSavedData savedData){
        chunkAnchorBlockArraySavedData = savedData;
        intiChunks();
    }

    /**
     * Initializes the chunks (currently does nothing, but could be extended).
     */
    private static void intiChunks(){
        // No current implementation; placeholder for future logic.
    }

    /**
     * Retrieves the block ID of the chunk at the given position.
     *
     * @param pos The block position to query.
     * @return The ID of the chunk at the position, or null if not found.
     */
    private static Integer getBlockId(BlockPos pos){
        Optional<ChunkAnchorBlockModel> chunkAnchorBlock = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList()
                .stream().filter(monitoredChunk -> monitoredChunk.isAtPos(pos)).findFirst();
        return chunkAnchorBlock.map(ChunkAnchorBlockModel::getId).orElse(null);
    }

    /**
     * Registers an anchor at the specified position if not already registered.
     *
     * @param state The current block state.
     * @param level The world level.
     * @param pos The position of the block.
     * @param oldState The previous block state.
     */
    public static void registerAnchor(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        // Skip on client side, as anchors are managed on the server.
        if(level.isClientSide){
            return;
        }

        // Check if the anchor already exists at the given position.
        boolean exist = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .anyMatch(monitoredChunk -> monitoredChunk.isAtPos(pos));

        // If not, register the new anchor.
        if(!exist){
            chunkAnchorBlockArraySavedData.addAnchor(new ChunkAnchorBlockModel(pos, level.dimension()));
        }
    }

    /**
     * Checks if an anchor can be removed from the specified position.
     *
     * @param blockPos The position of the block to check.
     * @param level The world level.
     * @return True if the anchor can be removed, otherwise false.
     */
    public static boolean canRemoveAnchor(BlockPos blockPos, Level level){
        // Skip on client side.
        if(level.isClientSide){
            return false;
        }

        // Check if no active anchor exists at the given position.
        return chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList().stream()
                .noneMatch(chunkAnchor -> chunkAnchor.isAtPos(blockPos) && chunkAnchor.isActive());
    }

    /**
     * Removes the anchor at the given position from the world.
     *
     * @param blockPos The position of the anchor to remove.
     * @param level The server world level.
     */
    public static void removeAnchor(BlockPos blockPos, ServerLevel level){
        // Skip on client side.
        if(level.isClientSide){
            return;
        }

        // Find the chunk anchor for the given position.
        Optional<ChunkAnchorBlockModel> chunkAnchor = chunkAnchorBlockArraySavedData.getAnchor(blockPos);

        if(chunkAnchor.isPresent()){
            // Remove the anchor from chunk loading and player tracking.
            removeAnchorLoading(chunkAnchor.get(), level);
            PlayerTracker.removeBlockFromAllPlayers(chunkAnchor.get());
            // Finally, remove the anchor from saved data.
            chunkAnchorBlockArraySavedData.removeAnchor(chunkAnchor.get());
        }
    }

    /**
     * Registers the given block position to the specified player.
     *
     * @param pos The position of the block to register.
     * @param level The world level.
     * @param playerId The UUID of the player.
     */
    public static void registerBlockToPlayer(BlockPos pos, Level level, UUID playerId){
        // Skip on client side.
        if(level.isClientSide){
            return;
        }

        // Get the block ID and register it for the player.
        Integer id = getBlockId(pos);
        if(id != null){
            PlayerTracker.addBlock(id, level, playerId);
        }
    }

    /**
     * Changes the status of the anchor with the given ID.
     * This includes updating the anchor's status and either adding or removing the chunk from loading.
     *
     * @param server The Minecraft server instance.
     * @param anchorId The ID of the anchor whose status is to be changed.
     */
    public static void changeAnchorStatus(MinecraftServer server, Integer anchorId) {
        // Find the anchor by its ID.
        Optional<ChunkAnchorBlockModel> anchor = chunkAnchorBlockArraySavedData.getAnchor(anchorId);
        if(anchor.isEmpty()){
            return;
        }

        // Update the anchor's status.
        Boolean isActive = chunkAnchorBlockArraySavedData.updateAnchorStatus(anchorId);

        // Get the corresponding server level for the anchor's dimension.
        ServerLevel level = server.getLevel(anchor.get().getDimension());
        if (isActive){
            // Add the chunk to the loading list if the anchor is active.
            addAnchorLoading(anchor.get(), level);
        }
        else {
            // Remove the chunk from the loading list if the anchor is inactive.
            removeAnchorLoading(anchor.get(), level);
        }
    }

    /**
     * Initializes the chunk anchors when the server starts.
     * This will add the chunks that have active anchors to the loading list.
     *
     * @param server The Minecraft server instance.
     */
    public static void init(MinecraftServer server) {
        // Iterate through the list of chunk anchors and add active ones to chunk loading.
        List<ChunkAnchorBlockModel> anchors = chunkAnchorBlockArraySavedData.getChunkAnchorBlockArrayList();

        for (ChunkAnchorBlockModel anchor : anchors) {
            if (!anchor.isActive()) continue;

            ServerLevel level = server.getLevel(anchor.getDimension());
            if (level == null) {
                continue;
            }

            // Add the chunk to loading for the active anchor.
            addAnchorLoading(anchor, level);
        }
    }

    /**
     * Removes the chunk associated with the anchor from the chunk monitoring system.
     *
     * @param anchor The anchor to remove.
     * @param level The server world level.
     */
    private static void removeAnchorLoading(ChunkAnchorBlockModel anchor, ServerLevel level){
        // Get the chunk position and remove monitoring.
        ChunkUpdateHandler.get(level).removeChunkMonitoring(new ChunkPos(anchor.getPos()));
    }

    /**
     * Adds the chunk associated with the anchor to the chunk monitoring system.
     *
     * @param anchor The anchor to add.
     * @param level The server world level.
     */
    private static void addAnchorLoading(ChunkAnchorBlockModel anchor, ServerLevel level){
        // Get the chunk position and add monitoring.
        ChunkUpdateHandler.get(level).addChunkMonitoring(new ChunkPos(anchor.getPos()));
    }
}
