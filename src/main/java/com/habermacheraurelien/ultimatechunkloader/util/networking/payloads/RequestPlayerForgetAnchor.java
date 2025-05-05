package com.habermacheraurelien.ultimatechunkloader.util.networking.payloads;

import com.habermacheraurelien.ultimatechunkloader.GUI.model.ScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestPlayerForgetAnchor(Integer anchorId) implements CustomPacketPayload {
    public static final String CUSTOM_ID = "player_forget_anchor_payload";

    public static final CustomPacketPayload.Type<RequestPlayerForgetAnchor> TYPE = new CustomPacketPayload.Type<>
            (ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, CUSTOM_ID));

    public final static StreamCodec<ByteBuf, RequestPlayerForgetAnchor> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RequestPlayerForgetAnchor::anchorId,
            RequestPlayerForgetAnchor::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
