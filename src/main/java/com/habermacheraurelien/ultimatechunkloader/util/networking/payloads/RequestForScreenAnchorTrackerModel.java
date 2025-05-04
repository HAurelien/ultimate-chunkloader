package com.habermacheraurelien.ultimatechunkloader.util.networking.payloads;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestForScreenAnchorTrackerModel(String playerUUID) implements CustomPacketPayload {
    public static final String CUSTOM_ID = "request_for_anchor_list_payload";

    public static final CustomPacketPayload.Type<RequestForScreenAnchorTrackerModel> TYPE = new CustomPacketPayload.Type<>
            (ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, CUSTOM_ID));

    public final static StreamCodec<ByteBuf, RequestForScreenAnchorTrackerModel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            RequestForScreenAnchorTrackerModel::playerUUID,
            RequestForScreenAnchorTrackerModel::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}