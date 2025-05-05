package com.habermacheraurelien.ultimatechunkloader.util.save;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * ListChunkAnchorSavedData is a custom class for handling saved data related to chunk anchors in Minecraft.
 * It is used to store a list of {@link ChunkAnchorBlockModel} objects in the server's data storage system.
 * The data is serialized and deserialized using NBT (Named Binary Tag) formats.
 */
public class ListChunkAnchorSavedData extends SavedData {

    // The unique identifier for this saved data
    public static final String DATA_NAME = UltimateChunkLoaderMod.MOD_ID + "." + "list_chunk_anchor";

    // List to store chunk anchor block models
    private final List<ChunkAnchorBlockModel> chunkAnchorBlockArrayList = new ArrayList<>();

    // Factory to create and deserialize this data from storage
    public static final ListChunkAnchorSavedData.Factory<ListChunkAnchorSavedData> FACTORY =
            new SavedData.Factory<>(
                    ListChunkAnchorSavedData::new,
                    ListChunkAnchorSavedData::deserialize
            );

    /**
     * Default constructor for ListChunkAnchorSavedData.
     */
    public ListChunkAnchorSavedData() {
    }

    /**
     * Deserializes the ListChunkAnchorSavedData from a given NBT tag.
     * This method reconstructs the data from a {@link CompoundTag} and populates the list of chunk anchors.
     *
     * @param tag the NBT tag containing the data to deserialize
     * @param provider a provider for the lookup of holder references (not used in this implementation)
     * @return a new ListChunkAnchorSavedData instance populated with deserialized chunk anchors
     */
    public static ListChunkAnchorSavedData deserialize(CompoundTag tag, HolderLookup.Provider provider) {
        ListChunkAnchorSavedData data = new ListChunkAnchorSavedData();

        // Deserialize chunk anchors
        if (tag.contains("chunk_anchors", 9)) { // 9 corresponds to a ListTag
            ListTag chunkAnchorsTag = tag.getList("chunk_anchors", 10); // 10 corresponds to CompoundTag in the ListTag
            chunkAnchorsTag.forEach(tagElement -> {
                ChunkAnchorBlockModel chunkAnchor = ChunkAnchorBlockModel.decode((CompoundTag) tagElement);
                data.chunkAnchorBlockArrayList.add(chunkAnchor);
            });
        }

        return data;
    }

    /**
     * Serializes the ListChunkAnchorSavedData to a {@link CompoundTag} so it can be saved in Minecraft's data storage.
     * This method stores the chunk anchors into an NBT tag.
     *
     * @param compoundTag the tag to save the data into
     * @param provider a provider for the lookup of holder references (not used in this implementation)
     * @return the updated compoundTag containing the serialized data
     */
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        // Serialize chunk anchors
        ListTag chunkAnchorsTag = new ListTag();
        for (ChunkAnchorBlockModel chunkAnchor : chunkAnchorBlockArrayList) {
            chunkAnchorsTag.add(chunkAnchor.encode()); // Assuming `encode()` serializes to NBT
        }
        compoundTag.put("chunk_anchors", chunkAnchorsTag);

        return compoundTag;
    }

    /**
     * Gets the list of chunk anchor block models stored in this saved data.
     *
     * @return a list of {@link ChunkAnchorBlockModel} objects
     */
    public List<ChunkAnchorBlockModel> getChunkAnchorBlockArrayList() {
        return chunkAnchorBlockArrayList;
    }

    /**
     * Adds a new chunk anchor to the list of chunk anchors.
     * Marks the data as dirty to ensure it will be saved.
     *
     * @param chunkAnchorBlockModel the {@link ChunkAnchorBlockModel} to add
     */
    public void addAnchor(ChunkAnchorBlockModel chunkAnchorBlockModel) {
        chunkAnchorBlockArrayList.add(chunkAnchorBlockModel);
        setDirty(); // Mark data as dirty to trigger a save
    }

    /**
     * Removes a chunk anchor from the list of chunk anchors.
     *
     * @param chunkAnchor the {@link ChunkAnchorBlockModel} to remove
     */
    public void removeAnchor(ChunkAnchorBlockModel chunkAnchor){
        chunkAnchorBlockArrayList.remove(chunkAnchor);
    }

    /**
     * Toggles the active status of a chunk anchor identified by its ID.
     * If the anchor is found, its active state is flipped, and the data is marked as dirty to ensure saving.
     *
     * @param anchorId the ID of the chunk anchor to update
     * @return the new active status of the anchor, or null if the anchor was not found
     */
    public Boolean updateAnchorStatus(Integer anchorId) {
        Optional<ChunkAnchorBlockModel> selectedAnchor = chunkAnchorBlockArrayList.stream()
                .filter(anchor -> Objects.equals(anchor.getId(), anchorId)).findFirst();
        if(selectedAnchor.isEmpty()){
            return null; // Anchor not found
        }
        selectedAnchor.get().setActive(!selectedAnchor.get().isActive());
        setDirty(); // Mark data as dirty to trigger a save
        return selectedAnchor.get().isActive();
    }

    /**
     * Retrieves a chunk anchor by its ID.
     *
     * @param anchorId the ID of the chunk anchor to retrieve
     * @return an {@link Optional} containing the chunk anchor if found, or empty if not
     */
    public Optional<ChunkAnchorBlockModel> getAnchor(Integer anchorId){
        return chunkAnchorBlockArrayList.stream().filter(anchor -> anchor.getId() == anchorId).findFirst();
    }

    /**
     * Retrieves a chunk anchor by its position.
     *
     * @param pos the position of the chunk anchor to retrieve
     * @return an {@link Optional} containing the chunk anchor if found, or empty if not
     */
    public Optional<ChunkAnchorBlockModel> getAnchor(BlockPos pos){
        return chunkAnchorBlockArrayList.stream().filter(anchor -> anchor.isAtPos(pos)).findFirst();
    }
}
