package net.pod.cnmb.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.event.ModClientEvents;
import net.pod.cnmb.util.ControllableMob;

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

    public static void handle(SetControllingPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ControllableMob mob = (ControllableMob) Minecraft.getInstance().level.getEntity(payload.mobId());
            ModClientEvents.controlling = payload.controlling() ? mob : null;
            mob.controller = payload.controlling() ? Minecraft.getInstance().player : null;
        });
    }
}
