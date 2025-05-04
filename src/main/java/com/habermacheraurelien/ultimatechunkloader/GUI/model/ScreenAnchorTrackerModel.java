package com.habermacheraurelien.ultimatechunkloader.GUI.model;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class ScreenAnchorTrackerModel {
    private static List<ChunkAnchorBlockModel> chunkAnchorBlockModelList;

    public static final Codec<ScreenAnchorTrackerModel> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.list(ChunkAnchorBlockModel.chunkAnchorBlockModelCodec).fieldOf("list_anchor")
                            .forGetter(ScreenAnchorTrackerModel::getChunkAnchorBlockModelList)
            ).apply(instance, ScreenAnchorTrackerModel::new));

    public ScreenAnchorTrackerModel(List<ChunkAnchorBlockModel> chunkAnchorBlockModelList){
        ScreenAnchorTrackerModel.chunkAnchorBlockModelList = chunkAnchorBlockModelList;
    }

    public List<ChunkAnchorBlockModel> getChunkAnchorBlockModelList() {
        return chunkAnchorBlockModelList;
    }

    public void setChunkAnchorBlockModelList(List<ChunkAnchorBlockModel> chunkAnchorBlockModelList) {
        ScreenAnchorTrackerModel.chunkAnchorBlockModelList = chunkAnchorBlockModelList;
    }

    @Override
    public String toString(){
        return "ScreenAnchorTrackerModel{list:" + chunkAnchorBlockModelList.toString() + "}";
    }
}
