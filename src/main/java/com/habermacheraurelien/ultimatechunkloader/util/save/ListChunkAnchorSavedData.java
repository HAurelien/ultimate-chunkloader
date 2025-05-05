package com.habermacheraurelien.ultimatechunkloader.util.save;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ListChunkAnchorSavedData extends SavedData {

    public static final String DATA_NAME = UltimateChunkLoaderMod.MOD_ID + "." + "list_chunk_anchor";

    private final List<ChunkAnchorBlockModel> chunkAnchorBlockArrayList = new ArrayList<>();

    public static final ListChunkAnchorSavedData.Factory<ListChunkAnchorSavedData> FACTORY =
            new SavedData.Factory<ListChunkAnchorSavedData>(
            ListChunkAnchorSavedData::new,
            ListChunkAnchorSavedData::deserialize
    );

    public ListChunkAnchorSavedData() {
    }

    // Deserialization logic using CompoundTag and Codec
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

    // Serialization logic using CompoundTag
    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        // Serialize chunk anchors
        ListTag chunkAnchorsTag = new ListTag();
        for (ChunkAnchorBlockModel chunkAnchor : chunkAnchorBlockArrayList) {
            chunkAnchorsTag.add(chunkAnchor.encode()); // Assuming `encode()` serializes to NBT
        }
        compoundTag.put("chunk_anchors", chunkAnchorsTag);

        return compoundTag;
    }

    public List<ChunkAnchorBlockModel> getChunkAnchorBlockArrayList() {
        return chunkAnchorBlockArrayList;
    }

    public void addAnchor(ChunkAnchorBlockModel chunkAnchorBlockModel) {
        chunkAnchorBlockArrayList.add(chunkAnchorBlockModel);
        setDirty();
    }

    public void removeAnchor(ChunkAnchorBlockModel chunkAnchor){
        chunkAnchorBlockArrayList.remove(chunkAnchor);
    }

    public Boolean updateAnchorStatus(Integer anchorId) {
        Optional<ChunkAnchorBlockModel> selectedAnchor = chunkAnchorBlockArrayList.stream()
                .filter(anchor -> anchor.getId() == anchorId).findFirst();
        if(selectedAnchor.isEmpty()){
            return null;
        }
        selectedAnchor.get().setActive(!selectedAnchor.get().isActive());
        setDirty();
        return selectedAnchor.get().isActive();
    }

    public Optional<ChunkAnchorBlockModel> getAnchor(Integer anchorId){
        return  chunkAnchorBlockArrayList.stream().filter(anchor -> anchor.getId() == anchorId).findFirst();
    }

    public Optional<ChunkAnchorBlockModel> getAnchor(BlockPos pos){
        return  chunkAnchorBlockArrayList.stream().filter(anchor -> anchor.isAtPos(pos)).findFirst();
    }
}