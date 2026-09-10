package net.pod.cnmb.networking.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;

public record SetControllingPayload(int mobId, boolean controlling) implements CustomPacketPayload {
    public static final Type<SetControllingPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "send_controlling"
            ));
    public static final StreamCodec<ByteBuf, SetControllingPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SetControllingPayload::mobId,
                    ByteBufCodecs.BOOL, SetControllingPayload::controlling,
                    SetControllingPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
