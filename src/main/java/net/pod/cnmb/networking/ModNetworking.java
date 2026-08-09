package net.pod.cnmb.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;

public class ModNetworking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                GunTriggerPayload.TYPE,
                GunTriggerPayload.STREAM_CODEC,
                ModNetworking::handleGunTrigger
        );
    }
    public record GunTriggerPayload(boolean pressed) implements CustomPacketPayload {
        public static final Type<GunTriggerPayload> TYPE =
                new Type<>(
                        ResourceLocation.fromNamespaceAndPath(
                                NeedMoreBulletsMod.MODID,
                                "gun_trigger"
                        )
                );

        public static final StreamCodec<ByteBuf, GunTriggerPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.BOOL,
                        GunTriggerPayload::pressed,
                        GunTriggerPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    private static void handleGunTrigger(
            GunTriggerPayload payload,
            IPayloadContext context) {

        Player player = context.player();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack stack = serverPlayer.getMainHandItem();

        if (!(stack.getItem() instanceof AbstractGunItem gun)) {
            return;
        }

        if (payload.pressed()) {
            gun.startFiring(serverPlayer);
        } else {
            gun.stopFiring(serverPlayer);
        }
    }
}
