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


public class ChunkAnchorBlockModel {
    private static int ID = 0;
    private final int id;
    private final BlockPos pos;
    private final ResourceKey<Level> dimension;
    private Boolean active = false;
    private String name;

    public ChunkAnchorBlockModel(BlockPos _pos, ResourceKey<Level>  _dimension){
        pos = _pos;
        dimension = _dimension;
        id = ID;
        ID++;
    }

    public ChunkAnchorBlockModel(BlockPos _blockPos, ResourceKey<Level>  _dimension, Boolean _active, Integer _id, String _name) {
        if(_id >= ID){
            ID = _id+1;
        }
        id = _id;
        pos = _blockPos;
        active = _active;
        dimension = _dimension;
        name = _name;
    }

    public Integer getId(){
        return id;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean state){
        active = state;
    }

    public boolean isAtPos(BlockPos _pos){
        return pos.equals(_pos);
    }

    public BlockPos getPos(){
        return pos;
    }

    public ResourceKey<Level>  getDimension(){
        return dimension;
    }

    public static final Codec<ChunkAnchorBlockModel> chunkAnchorBlockModelCodec = RecordCodecBuilder
            .create(instance -> instance.group(
                    BlockPos.CODEC.fieldOf("block_pos").forGetter(ChunkAnchorBlockModel::getPos),
                    Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(ChunkAnchorBlockModel::getDimension),
                    Codec.BOOL.fieldOf("active").forGetter(ChunkAnchorBlockModel::isActive),
                    Codec.INT.fieldOf("id").forGetter(ChunkAnchorBlockModel::getId),
                    Codec.STRING.fieldOf("name").forGetter(ChunkAnchorBlockModel::getName)
            ).apply(instance, ChunkAnchorBlockModel::new));

    public String getName() {
        return name;
    }

    public void setName(String _name){
        name = _name;
    }

    public CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
        Optional<Tag> encoded  = chunkAnchorBlockModelCodec.encodeStart(NbtOps.INSTANCE, this).result();
        encoded.ifPresent(encodedTag -> tag.put("data", encodedTag));
        return tag;
    }


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