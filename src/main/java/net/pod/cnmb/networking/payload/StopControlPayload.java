package net.pod.cnmb.networking.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.util.ControllableMob;

public record StopControlPayload(int mobId) implements CustomPacketPayload {
    public static final Type<StopControlPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "send_stop_control"
            ));
    public static final StreamCodec<ByteBuf, StopControlPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, StopControlPayload::mobId,
                    StopControlPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StopControlPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ((ControllableMob)player.level().getEntity(payload.mobId())).stop();
            }
        });
    }
}
