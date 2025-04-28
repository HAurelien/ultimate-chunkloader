package com.habermacheraurelien.ultimatechunkloader.util.save;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListPlayerDiscoveredAnchorSavedData extends SavedData {

    public static final String DATA_NAME = UltimateChunkLoaderMod.MOD_ID + "." + "list_player_discovered_anchor";
    public static final String ANCHOR_DATA_NAME = "discovered_anchors_per_player";
    private List<PlayerAnchorTrackerModel> playerAnchorTrackerModels = new ArrayList<>();

    public static final ListPlayerDiscoveredAnchorSavedData.Factory<ListPlayerDiscoveredAnchorSavedData> FACTORY =
            new SavedData.Factory<ListPlayerDiscoveredAnchorSavedData>(
            ListPlayerDiscoveredAnchorSavedData::new,
            ListPlayerDiscoveredAnchorSavedData::deserialize
    );

    private static ListPlayerDiscoveredAnchorSavedData deserialize(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListPlayerDiscoveredAnchorSavedData data = new ListPlayerDiscoveredAnchorSavedData();

        // Deserialize player anchor trackers
        if (compoundTag.contains(ANCHOR_DATA_NAME, 9)) {
            ListTag playerAnchorsTag = compoundTag.getList(ANCHOR_DATA_NAME, 10);
            playerAnchorsTag.forEach(tagElement -> {
                PlayerAnchorTrackerModel playerAnchor = PlayerAnchorTrackerModel.decode((CompoundTag) tagElement);
                data.playerAnchorTrackerModels.add(playerAnchor);
            });
        }
        return data;
    }

    public void addPlayerAnchor(PlayerAnchorTrackerModel model) {
        playerAnchorTrackerModels.add(model);
        setDirty(); // Important: Marks the data as changed so it saves
    }

    public PlayerAnchorTrackerModel getPlayerAnchorFromPlayer(UUID playerId){
        return playerAnchorTrackerModels.stream()
                .filter(anchorList -> anchorList.getPlayerId().equals(playerId))
                .findFirst().orElseGet(null);
    }

    public PlayerAnchorTrackerModel getPlayerAssociatedWithAnchorId(int anchorId){
        return playerAnchorTrackerModels.stream()
                .filter(anchorList -> anchorList.contains(anchorId))
                .findFirst().orElseGet(null);
    }

    // Deserialization logic

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {

        // Serialize player anchor trackers
        ListTag playerAnchorsTag = new ListTag();
        for (PlayerAnchorTrackerModel playerAnchor : playerAnchorTrackerModels) {
            playerAnchorsTag.add(playerAnchor.encode()); // Assuming `encode()` serializes to NBT
        }
        compoundTag.put(ANCHOR_DATA_NAME, playerAnchorsTag);

        return compoundTag;
    }
}
