package net.pod.cnmb.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;

public record LeftClickPayload() implements CustomPacketPayload {

    public static final Type<LeftClickPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "left_click"
            ));

    public static final StreamCodec<ByteBuf, LeftClickPayload> STREAM_CODEC =
            StreamCodec.unit(new LeftClickPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}