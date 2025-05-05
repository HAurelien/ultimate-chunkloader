package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.util.*;

/**
 * This model is responsible for tracking the anchors discovered by a specific player.
 * Each player has a unique ID, and this model stores the properties of the anchors they have discovered.
 */
public class PlayerAnchorTrackerModel {

    /** The unique ID of the player associated with this tracker. */
    private final UUID playerId;

    /** A map of anchor IDs to their respective properties discovered by the player. */
    private final Map<Integer, PlayerAnchorPropertiesModel> chunksDiscovered = new HashMap<>();

    /**
     * Constructs a new {@link PlayerAnchorTrackerModel} for a player using their UUID.
     *
     * @param playerId the unique ID of the player
     */
    public PlayerAnchorTrackerModel(UUID playerId) {
        this.playerId = playerId;
    }

    /**
     * Constructs a new {@link PlayerAnchorTrackerModel} using a list of anchor properties and a player ID string.
     * The anchor properties are associated with the player's ID.
     *
     * @param anchors the list of anchor properties to be added
     * @param playerIdString the string representation of the player's UUID
     */
    public PlayerAnchorTrackerModel(List<PlayerAnchorPropertiesModel> anchors, String playerIdString) {
        this.playerId = UUID.fromString(playerIdString);
        for (PlayerAnchorPropertiesModel model : anchors) {
            chunksDiscovered.put(model.getId(), model);
        }
    }

    /**
     * Gets the UUID of the player associated with this model.
     *
     * @return the player's UUID
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the string representation of the player's UUID.
     *
     * @return the player's UUID as a string
     */
    private String getPlayerIdAsString() {
        return playerId.toString();
    }

    /**
     * Checks whether the player has discovered an anchor with the specified ID.
     *
     * @param anchorId the ID of the anchor
     * @return {@code true} if the anchor has been discovered, otherwise {@code false}
     */
    public boolean contains(Integer anchorId) {
        return chunksDiscovered.containsKey(anchorId);
    }

    /**
     * Adds an anchor to the player's discovered anchors list.
     * If the anchor already exists, it will not be added again.
     *
     * @param anchorId the ID of the anchor to be added
     */
    public void addAnchor(Integer anchorId) {
        chunksDiscovered.putIfAbsent(anchorId, new PlayerAnchorPropertiesModel(anchorId));
    }

    /**
     * Removes an anchor from the player's discovered anchors list.
     *
     * @param anchorId the ID of the anchor to be removed
     */
    public void removeAnchor(Integer anchorId) {
        chunksDiscovered.remove(anchorId);
    }

    /**
     * Clears all anchors from the player's discovered anchors list.
     */
    public void clearAnchors() {
        chunksDiscovered.clear();
    }

    /**
     * Gets a collection of all the discovered anchor properties for the player.
     *
     * @return a collection of {@link PlayerAnchorPropertiesModel} objects representing the discovered anchors
     */
    public Collection<PlayerAnchorPropertiesModel> getAnchorList() {
        return chunksDiscovered.values();
    }

    /**
     * Gets the name of the anchor identified by the specified ID.
     *
     * @param anchorId the ID of the anchor whose name is to be retrieved
     * @return the name of the anchor, or {@code null} if no anchor with that ID is found
     */
    public String getAnchorNameById(Integer anchorId) {
        return Optional.ofNullable(chunksDiscovered.get(anchorId))
                .map(PlayerAnchorPropertiesModel::getName)
                .orElse(null);
    }

    /**
     * Sets the name of the anchor identified by the specified ID.
     *
     * @param anchorId the ID of the anchor whose name is to be set
     * @param name the new name for the anchor
     */
    public void setAnchorNameById(Integer anchorId, String name) {
        PlayerAnchorPropertiesModel model = chunksDiscovered.get(anchorId);
        if (model != null) {
            model.setName(name);
        }
    }

    /**
     * A codec for serializing and deserializing {@link PlayerAnchorTrackerModel} objects.
     * This codec allows the model to be safely encoded and decoded for storage or transmission.
     */
    public static final Codec<PlayerAnchorTrackerModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(PlayerAnchorPropertiesModel.CODEC)
                    .fieldOf("anchor_properties")
                    .forGetter(model -> new ArrayList<>(model.getAnchorList())),
            Codec.STRING.fieldOf("player_uuid").forGetter(PlayerAnchorTrackerModel::getPlayerIdAsString)
    ).apply(instance, PlayerAnchorTrackerModel::new));

    /**
     * Encodes this {@link PlayerAnchorTrackerModel} into a {@link CompoundTag}.
     *
     * @return a {@link CompoundTag} containing the encoded model data
     */
    public CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(System.err::println)
                .ifPresent(encoded -> tag.put("data", encoded));
        return tag;
    }

    /**
     * Decodes a {@link PlayerAnchorTrackerModel} from a {@link CompoundTag}.
     *
     * @param tag the {@link CompoundTag} containing the encoded model data
     * @return the decoded {@link PlayerAnchorTrackerModel}
     */
    public static PlayerAnchorTrackerModel decode(CompoundTag tag) {
        CompoundTag data = tag.getCompound("data");
        return CODEC.decode(NbtOps.INSTANCE, data)
                .resultOrPartial(System.err::println)
                .map(Pair::getFirst)
                .orElseThrow(() -> new IllegalArgumentException("Failed to deserialize PlayerAnchorTrackerModel"));
    }

    /**
     * Returns a string representation of the {@link PlayerAnchorTrackerModel}.
     * This includes the player's UUID and the list of discovered anchors.
     *
     * @return a string representation of the player and their discovered anchors
     */
    @Override
    public String toString() {
        return "{player : " + playerId + ", chunk list : " + chunksDiscovered + "}";
    }
}
