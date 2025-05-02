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
    private final List<Integer> chunksDiscovered = new ArrayList<>();

    public PlayerAnchorTrackerModel(UUID _player) {
        this.playerId = _player;
    }

    public PlayerAnchorTrackerModel(List<Integer> _chunksDiscovered, String _playerId) {
        this.playerId = UUID.fromString(_playerId);
        this.chunksDiscovered.addAll(_chunksDiscovered);
    }

    public UUID getPlayerId(){
        return playerId;
    }

    private String getPlayerIdAsString(){
        return playerId.toString();
    }

    public boolean contains(Integer anchorId){
        return chunksDiscovered.contains(anchorId);
    }

    public void addAnchor(Integer anchorId){
        if(!chunksDiscovered.contains(anchorId)){
            chunksDiscovered.add(anchorId);
        }
    }

    public void removeAnchor(Integer anchorId){
        chunksDiscovered.remove(anchorId);
    }

    public void clearAnchors(){
        chunksDiscovered.clear();
    }

    public List<Integer> getIdList(){
        return chunksDiscovered;
    }

    public String getAnchorNameByIndex(int id){
        return chunksDiscovered.get(id).toString();
    }

    public static final Codec<PlayerAnchorTrackerModel> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.list(Codec.INT).fieldOf("chunk_ids").forGetter(PlayerAnchorTrackerModel::getIdList),
                    Codec.STRING.fieldOf("uuid").forGetter(PlayerAnchorTrackerModel::getPlayerIdAsString)
            ).apply(instance, PlayerAnchorTrackerModel::new));


    public CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this)  // Use NbtOps for consistent NBT serialization
                .resultOrPartial(System.err::println)
                .ifPresent(encoded -> tag.put("data", encoded));  // Store the actual NBT data, not a string
        return tag;
    }

    // Deserialization method (Decode from CompoundTag)
    public static PlayerAnchorTrackerModel decode(CompoundTag tag) {
        // Deserialize from NBT using Codec
        CompoundTag data = tag.getCompound("data");

        DataResult<Pair<PlayerAnchorTrackerModel, Tag>> result = CODEC.decode(NbtOps.INSTANCE, data);

        return result.resultOrPartial(System.err::println)
                .map(Pair::getFirst)
                .orElseThrow(() -> new IllegalArgumentException("Failed to deserialize ChunkAnchorBlockModel"));
    }

    @Override
    public String toString(){
        return "{player : " + playerId.toString() + ", chunk list : " + chunksDiscovered.toString() + "}";
    }
}
