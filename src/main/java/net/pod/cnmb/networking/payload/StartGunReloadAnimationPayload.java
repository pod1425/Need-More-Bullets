package net.pod.cnmb.networking.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;

public record StartGunReloadAnimationPayload(Boolean isMainHand) implements CustomPacketPayload {
    public static final Type<StartGunReloadAnimationPayload> TYPE =
            new Type<>(NeedMoreBulletsMod.asResource("payload_start_gun_reload_animation"));
    public static final StreamCodec<ByteBuf, StartGunReloadAnimationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, StartGunReloadAnimationPayload::isMainHand,
                    StartGunReloadAnimationPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StartGunReloadAnimationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                NeedMoreBulletsMod.LOGGER.debug("enqueue custom attack");
                ItemStack stack = payload.isMainHand ? player.getMainHandItem() : player.getOffhandItem();
                if (stack.getItem() instanceof AbstractGunItem gun) gun.reload(player, stack);
            }
            System.out.println("get payload");
        });
    }
}
