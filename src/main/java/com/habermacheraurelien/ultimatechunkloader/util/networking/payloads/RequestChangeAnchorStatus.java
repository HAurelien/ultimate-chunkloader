package com.habermacheraurelien.ultimatechunkloader.util.networking.payloads;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestChangeAnchorStatus(Integer anchorId) implements CustomPacketPayload {
    public static final String CUSTOM_ID = "request_for_anchor_status_change";

    public static final CustomPacketPayload.Type<RequestChangeAnchorStatus> TYPE = new CustomPacketPayload.Type<>
            (ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, CUSTOM_ID));

    public final static StreamCodec<ByteBuf, RequestChangeAnchorStatus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RequestChangeAnchorStatus::anchorId,
            RequestChangeAnchorStatus::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
