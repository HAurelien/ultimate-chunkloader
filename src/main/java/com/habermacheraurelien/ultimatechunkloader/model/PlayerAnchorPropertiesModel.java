package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
/**
 * Represents the properties of a player's anchor block.
 * This model is used to store and transmit data about a player's anchor, such as its ID and name.
 */
public class PlayerAnchorPropertiesModel {

    /** The unique identifier of the anchor. */
    private Integer id;

    /** The name of the anchor. */
    private String name;

    /**
     * Constructs a new {@link PlayerAnchorPropertiesModel} with the specified ID.
     * The name of the anchor is automatically set to "Anchor <id>".
     *
     * @param _id the unique ID of the anchor
     */
    public PlayerAnchorPropertiesModel(Integer _id) {
        id = _id;
        name = "Anchor " + _id;
    }

    /**
     * Constructs a new {@link PlayerAnchorPropertiesModel} with the specified ID and name.
     *
     * @param _id the unique ID of the anchor
     * @param _name the name of the anchor
     */
    public PlayerAnchorPropertiesModel(Integer _id, String _name) {
        id = _id;
        name = _name;
    }

    /**
     * Gets the unique ID of the anchor.
     *
     * @return the ID of the anchor
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique ID of the anchor.
     *
     * @param id the new ID of the anchor
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the anchor.
     *
     * @return the name of the anchor
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the anchor.
     *
     * @param name the new name for the anchor
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A codec for serializing and deserializing {@link PlayerAnchorPropertiesModel} objects.
     * The codec supports both reading from and writing to data streams.
     */
    public static final Codec<PlayerAnchorPropertiesModel> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.INT.fieldOf("id").forGetter(PlayerAnchorPropertiesModel::getId),
                    Codec.STRING.fieldOf("name").forGetter(PlayerAnchorPropertiesModel::getName)
            ).apply(instance, PlayerAnchorPropertiesModel::new));

    /**
     * A stream codec for encoding and decoding {@link PlayerAnchorPropertiesModel} objects
     * to and from a {@link FriendlyByteBuf}. It allows the model to be safely serialized and
     * deserialized across network channels.
     */
    public static final StreamCodec<FriendlyByteBuf, PlayerAnchorPropertiesModel> PLAYER_ANCHOR_PROPERTIES_CODEC =
            new StreamCodec<>() {

                /**
                 * Decodes a {@link PlayerAnchorPropertiesModel} from a {@link FriendlyByteBuf}.
                 *
                 * @param buf the byte buffer to read from
                 * @return the decoded {@link PlayerAnchorPropertiesModel}
                 */
                @Override
                public @NotNull PlayerAnchorPropertiesModel decode(FriendlyByteBuf buf) {
                    int id = buf.readInt();
                    String name = buf.readUtf(); // Read the name from the buffer
                    return new PlayerAnchorPropertiesModel(id, name);
                }

                /**
                 * Encodes a {@link PlayerAnchorPropertiesModel} into a {@link FriendlyByteBuf}.
                 *
                 * @param buf the byte buffer to write to
                 * @param value the {@link PlayerAnchorPropertiesModel} to encode
                 */
                @Override
                public void encode(FriendlyByteBuf buf, PlayerAnchorPropertiesModel value) {
                    buf.writeInt(value.getId());
                    buf.writeUtf(value.getName()); // Write the name into the buffer
                }
            };
}
