package net.pod.cnmb.networking.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.util.CustomAttack;

public record CustomAttackPayload(int mobId) implements CustomPacketPayload {
    public static final Type<CustomAttackPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "send_custom_attack"
            ));
    public static final StreamCodec<ByteBuf, CustomAttackPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, CustomAttackPayload::mobId,
                    CustomAttackPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CustomAttackPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                NeedMoreBulletsMod.LOGGER.debug("enqueue custom attack");
                ((CustomAttack)player.level().getEntity(payload.mobId())).attack();
            }
        });
    }
}
