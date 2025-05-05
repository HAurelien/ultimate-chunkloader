package com.habermacheraurelien.ultimatechunkloader.util.networking.payloads;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorPropertiesModel;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestChangeAnchorPlayerProperty(PlayerAnchorPropertiesModel anchorProperties) implements CustomPacketPayload {
    public static final String CUSTOM_ID = "request_for_anchor_player_properties_change";

    public static final Type<RequestChangeAnchorPlayerProperty> TYPE = new Type<>
            (ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, CUSTOM_ID));

    public static final StreamCodec<ByteBuf, RequestChangeAnchorPlayerProperty> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull RequestChangeAnchorPlayerProperty decode(@NotNull ByteBuf buf) {
                    FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(buf);
                    PlayerAnchorPropertiesModel props = PlayerAnchorPropertiesModel.PLAYER_ANCHOR_PROPERTIES_CODEC
                            .decode(friendlyBuf);
                    return new RequestChangeAnchorPlayerProperty(props);
                }

                @Override
                public void encode(@NotNull ByteBuf buf, RequestChangeAnchorPlayerProperty value) {
                    FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(buf);
                    PlayerAnchorPropertiesModel.PLAYER_ANCHOR_PROPERTIES_CODEC
                            .encode(friendlyBuf, value.anchorProperties());
                }
            };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
