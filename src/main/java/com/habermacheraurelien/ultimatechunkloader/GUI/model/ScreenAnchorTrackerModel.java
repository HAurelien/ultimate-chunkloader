package com.habermacheraurelien.ultimatechunkloader.GUI.model;

import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * @param chunkAnchorBlockModelList List of chunk anchor block models known to this instance.
 */
public record ScreenAnchorTrackerModel(List<ChunkAnchorBlockModel> chunkAnchorBlockModelList) {

    /**
     * Codec used for serialization and deserialization of the anchor tracker model.
     */
    public static final Codec<ScreenAnchorTrackerModel> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.list(ChunkAnchorBlockModel.chunkAnchorBlockModelCodec).fieldOf("list_anchor")
                            .forGetter(ScreenAnchorTrackerModel::chunkAnchorBlockModelList)
            ).apply(instance, ScreenAnchorTrackerModel::new));

    /**
     * Constructs a new screen anchor tracker model with the provided list of anchors.
     *
     * @param chunkAnchorBlockModelList The list of anchors.
     */
    public ScreenAnchorTrackerModel {
    }

    /**
     * Gets the list of anchor models tracked by this instance.
     *
     * @return The list of {@link ChunkAnchorBlockModel}.
     */
    @Override
    public List<ChunkAnchorBlockModel> chunkAnchorBlockModelList() {
        return chunkAnchorBlockModelList;
    }

    @Override
    public String toString() {
        return "ScreenAnchorTrackerModel{list:" + chunkAnchorBlockModelList + "}";
    }
}
