package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.*;
public class PlayerAnchorTrackerModel {
    private final UUID playerId;
    private final Map<Integer, PlayerAnchorPropertiesModel> chunksDiscovered = new HashMap<>();

    public PlayerAnchorTrackerModel(UUID playerId) {
        this.playerId = playerId;
    }

    public PlayerAnchorTrackerModel(List<PlayerAnchorPropertiesModel> anchors, String playerIdString) {
        this.playerId = UUID.fromString(playerIdString);
        for (PlayerAnchorPropertiesModel model : anchors) {
            chunksDiscovered.put(model.getId(), model);
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    private String getPlayerIdAsString() {
        return playerId.toString();
    }

    public boolean contains(Integer anchorId) {
        return chunksDiscovered.containsKey(anchorId);
    }

    public void addAnchor(Integer anchorId) {
        chunksDiscovered.putIfAbsent(anchorId, new PlayerAnchorPropertiesModel(anchorId));
    }

    public void removeAnchor(Integer anchorId) {
        chunksDiscovered.remove(anchorId);
    }

    public void clearAnchors() {
        chunksDiscovered.clear();
    }

    public Collection<PlayerAnchorPropertiesModel> getAnchorList() {
        return chunksDiscovered.values();
    }

    public String getAnchorNameById(Integer anchorId) {
        return Optional.ofNullable(chunksDiscovered.get(anchorId))
                .map(PlayerAnchorPropertiesModel::getName)
                .orElse(null);
    }

    public void setAnchorNameById(Integer anchorId, String name) {
        PlayerAnchorPropertiesModel model = chunksDiscovered.get(anchorId);
        if (model != null) {
            model.setName(name);
        }
    }

    public static final Codec<PlayerAnchorTrackerModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(PlayerAnchorPropertiesModel.CODEC)
                    .fieldOf("anchor_properties")
                    .forGetter(model -> new ArrayList<>(model.getAnchorList())),
            Codec.STRING.fieldOf("player_uuid").forGetter(PlayerAnchorTrackerModel::getPlayerIdAsString)
    ).apply(instance, PlayerAnchorTrackerModel::new));

    public CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(System.err::println)
                .ifPresent(encoded -> tag.put("data", encoded));
        return tag;
    }

    public static PlayerAnchorTrackerModel decode(CompoundTag tag) {
        CompoundTag data = tag.getCompound("data");
        return CODEC.decode(NbtOps.INSTANCE, data)
                .resultOrPartial(System.err::println)
                .map(Pair::getFirst)
                .orElseThrow(() -> new IllegalArgumentException("Failed to deserialize PlayerAnchorTrackerModel"));
    }

    @Override
    public String toString() {
        return "{player : " + playerId + ", chunk list : " + chunksDiscovered + "}";
    }
}
