package com.habermacheraurelien.ultimatechunkloader.model;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.UUID;

public class ChunkAnchorBlockEntity {
    private static int ID = 0;  // TODO: Load ID from save
    private final int id;
    private final BlockPos pos;
    private final DimensionType dimension;
    private boolean active = false;

    public ChunkAnchorBlockEntity(BlockPos _pos, DimensionType _dimension){
        pos = _pos;
        dimension = _dimension;
        id = ID;
        ID++;
    }

    public int getId(){
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

    public DimensionType getDimension(){
        return dimension;
    }
}
