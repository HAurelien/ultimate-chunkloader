package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PlayerAnchorPropertiesModel {
    public PlayerAnchorPropertiesModel(Integer _id){
        id = _id;
        name = "Anchor " + _id;
    }

    public PlayerAnchorPropertiesModel(Integer _id, String _name){
        id = _id;
        name = _name;
    }
    private Integer id;
    private String name;

    public static Codec<PlayerAnchorPropertiesModel> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.INT.fieldOf("id").forGetter(PlayerAnchorPropertiesModel::getId),
                    Codec.STRING.fieldOf("name").forGetter(PlayerAnchorPropertiesModel::getName)
            ).apply(instance, PlayerAnchorPropertiesModel::new));

    public static final StreamCodec<FriendlyByteBuf, PlayerAnchorPropertiesModel> PLAYER_ANCHOR_PROPERTIES_CODEC =
            new StreamCodec<>() {
                @Override
                public PlayerAnchorPropertiesModel decode(FriendlyByteBuf buf) {
                    int id = buf.readInt();
                    String name = buf.readUtf(); // readUtf for reading strings safely
                    return new PlayerAnchorPropertiesModel(id, name);
                }

                @Override
                public void encode(FriendlyByteBuf buf, PlayerAnchorPropertiesModel value) {
                    buf.writeInt(value.getId());
                    buf.writeUtf(value.getName()); // writeUtf for writing strings
                }
            };
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
