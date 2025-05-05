package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Represents an anchor block in the world, with a position, a dimension, and an active state.
 * This model is used to track chunks that are marked for anchoring by players.
 *
 * <p>This class provides functionality for serialization, deserialization, and managing the state of the anchor.</p>
 */
public class ChunkAnchorBlockModel {

    /** A static ID counter used to generate unique IDs for anchor blocks. */
    private static int ID = 0;

    /** The unique ID of this anchor block. */
    private final int id;

    /** The position of the anchor block in the world. */
    private final BlockPos pos;

    /** The dimension in which the anchor block resides. */
    private final ResourceKey<Level> dimension;

    /** Whether this anchor block is currently active. */
    private Boolean active = false;

    /** The name of the anchor block, as given by the player. */
    private String name;

    /**
     * Constructs a new {@link ChunkAnchorBlockModel} with the specified position and dimension.
     * The ID is automatically assigned, and the anchor is initially inactive.
     *
     * @param _pos the position of the anchor block
     * @param _dimension the dimension where the anchor is located
     */
    public ChunkAnchorBlockModel(BlockPos _pos, ResourceKey<Level> _dimension) {
        pos = _pos;
        dimension = _dimension;
        id = ID;
        ID++;
    }

    /**
     * Constructs a new {@link ChunkAnchorBlockModel} with the specified properties, including a given ID and name.
     *
     * @param _blockPos the position of the anchor block
     * @param _dimension the dimension where the anchor is located
     * @param _active whether the anchor block is active
     * @param _id the unique ID of the anchor block
     * @param _name the name of the anchor block
     */
    public ChunkAnchorBlockModel(BlockPos _blockPos, ResourceKey<Level> _dimension, Boolean _active, Integer _id, String _name) {
        if (_id >= ID) {
            ID = _id + 1;
        }
        id = _id;
        pos = _blockPos;
        active = _active;
        dimension = _dimension;
        name = _name;
    }

    /**
     * Gets the unique ID of this anchor block.
     *
     * @return the ID of the anchor block
     */
    public Integer getId() {
        return id;
    }

    /**
     * Checks if the anchor block is active.
     *
     * @return true if the anchor block is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active state of the anchor block.
     *
     * @param state true to activate the anchor, false to deactivate
     */
    public void setActive(boolean state) {
        active = state;
    }

    /**
     * Checks if this anchor block is located at the given position.
     *
     * @param _pos the position to check
     * @return true if this anchor block is at the specified position, false otherwise
     */
    public boolean isAtPos(BlockPos _pos) {
        return pos.equals(_pos);
    }

    /**
     * Gets the position of the anchor block.
     *
     * @return the position of the anchor block
     */
    public BlockPos getPos() {
        return pos;
    }

    /**
     * Gets the dimension of the anchor block.
     *
     * @return the dimension of the anchor block
     */
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    /**
     * Codec used to serialize and deserialize {@link ChunkAnchorBlockModel} objects.
     */
    public static final Codec<ChunkAnchorBlockModel> chunkAnchorBlockModelCodec = RecordCodecBuilder
            .create(instance -> instance.group(
                    BlockPos.CODEC.fieldOf("block_pos").forGetter(ChunkAnchorBlockModel::getPos),
                    Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(ChunkAnchorBlockModel::getDimension),
                    Codec.BOOL.fieldOf("active").forGetter(ChunkAnchorBlockModel::isActive),
                    Codec.INT.fieldOf("id").forGetter(ChunkAnchorBlockModel::getId),
                    Codec.STRING.fieldOf("name").forGetter(ChunkAnchorBlockModel::getName)
            ).apply(instance, ChunkAnchorBlockModel::new));

    /**
     * Gets the name of the anchor block.
     *
     * @return the name of the anchor block
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the anchor block.
     *
     * @param _name the new name for the anchor block
     */
    public void setName(String _name) {
        name = _name;
    }

    /**
     * Encodes this anchor block model to a {@link CompoundTag}.
     *
     * @return a {@link CompoundTag} containing the serialized data of this anchor block
     */
    public CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
        Optional<Tag> encoded = chunkAnchorBlockModelCodec.encodeStart(NbtOps.INSTANCE, this).result();
        encoded.ifPresent(encodedTag -> tag.put("data", encodedTag));
        return tag;
    }

    /**
     * Decodes a {@link ChunkAnchorBlockModel} from the provided {@link CompoundTag}.
     *
     * @param tag the {@link CompoundTag} to decode from
     * @return the deserialized {@link ChunkAnchorBlockModel} object
     * @throws IllegalArgumentException if the deserialization fails
     */
    public static ChunkAnchorBlockModel decode(CompoundTag tag) {
        // Extract the data from the "data" key in the CompoundTag
        CompoundTag data = tag.getCompound("data");

        // Deserialize the object using the codec from the extracted data
        DataResult<Pair<ChunkAnchorBlockModel, Tag>> result = chunkAnchorBlockModelCodec.decode(NbtOps.INSTANCE, data);

        return result.resultOrPartial(System.err::println)
                .map(Pair::getFirst)
                .orElseThrow(() -> new IllegalArgumentException("Failed to deserialize ChunkAnchorBlockModel"));
    }
}
