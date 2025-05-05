package com.habermacheraurelien.ultimatechunkloader.util.save;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListPlayerDiscoveredAnchorSavedData extends SavedData {

    public static final String DATA_NAME = UltimateChunkLoaderMod.MOD_ID + "." + "list_player_discovered_anchor";
    public static final String ANCHOR_DATA_NAME = "discovered_anchors_per_player";

    private final Map<UUID, PlayerAnchorTrackerModel> playerAnchorTrackers = new HashMap<>();

    public static final SavedData.Factory<ListPlayerDiscoveredAnchorSavedData> FACTORY =
            new SavedData.Factory<>(ListPlayerDiscoveredAnchorSavedData::new, ListPlayerDiscoveredAnchorSavedData::deserialize);

    private static ListPlayerDiscoveredAnchorSavedData deserialize(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListPlayerDiscoveredAnchorSavedData data = new ListPlayerDiscoveredAnchorSavedData();

        if (compoundTag.contains(ANCHOR_DATA_NAME, 9)) {
            ListTag playerAnchorsTag = compoundTag.getList(ANCHOR_DATA_NAME, 10);
            for (Tag tagElement : playerAnchorsTag) {
                PlayerAnchorTrackerModel tracker = PlayerAnchorTrackerModel.decode((CompoundTag) tagElement);
                data.playerAnchorTrackers.put(tracker.getPlayerId(), tracker); // ✅ O(1) insert
            }
        }

        return data;
    }

    public void addPlayerAnchor(PlayerAnchorTrackerModel model) {
        playerAnchorTrackers.put(model.getPlayerId(), model);
        setDirty();
    }

    public PlayerAnchorTrackerModel getPlayerAnchorFromPlayer(UUID playerId) {
        return playerAnchorTrackers.computeIfAbsent(playerId, PlayerAnchorTrackerModel::new);
    }

    public List<PlayerAnchorTrackerModel> getAllPlayersPlayerAssociatedWithAnchorId(int anchorId) {
        return playerAnchorTrackers.values().stream()
                .filter(tracker -> tracker.contains(anchorId))
                .toList(); // unchanged
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag playerAnchorsTag = new ListTag();
        for (PlayerAnchorTrackerModel tracker : playerAnchorTrackers.values()) {
            playerAnchorsTag.add(tracker.encode());
        }
        compoundTag.put(ANCHOR_DATA_NAME, playerAnchorsTag);
        return compoundTag;
    }

    public void removeFromAllPlayers(int anchorId) {
        playerAnchorTrackers.values().forEach(tracker -> {
            if (tracker.contains(anchorId)) {
                tracker.removeAnchor(anchorId);
            }
        });
        setDirty();
    }

    public void forgetAnchorForPlayer(ServerPlayer player, Integer integer) {
        getPlayerAnchorFromPlayer(player.getUUID()).removeAnchor(integer);
        setDirty();
    }
}
