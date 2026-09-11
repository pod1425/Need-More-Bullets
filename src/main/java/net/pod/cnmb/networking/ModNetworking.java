package net.pod.cnmb.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.GunPlayerData;
import net.pod.cnmb.registry.ModPlayerAttachments;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ModNetworking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                GunTriggerPayload.TYPE,
                GunTriggerPayload.STREAM_CODEC,
                ModNetworking::handleGunTrigger
        );

        registrar.playToServer(
                RemoveAttachmentPayload.TYPE,
                RemoveAttachmentPayload.STREAM_CODEC,
                ModNetworking::handleRemoveAttachment
        );
    }
    public enum GunHand {
        MAIN_HAND(0),
        OFF_HAND(1),
        BOTH(2);

        private final int id;

        GunHand(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static GunHand fromId(int id) {
            return switch (id) {
                case 0 -> MAIN_HAND;
                case 1 -> OFF_HAND;
                case 2 -> BOTH;
                default -> throw new IllegalArgumentException("Unknown GunHand id: " + id);
            };
        }
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public record GunTriggerPayload(boolean pressed, GunHand hand) implements CustomPacketPayload {

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
                        ByteBufCodecs.VAR_INT.map(
                                GunHand::fromId,
                                GunHand::getId
                        ),
                        GunTriggerPayload::hand,
                        GunTriggerPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static void handleGunTrigger(GunTriggerPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        GunPlayerData data = player.getData(ModPlayerAttachments.GUN_DATA);

        data.setTriggerPressed(payload.pressed());
        data.setTriggerHand(payload.hand());

        if (!payload.pressed()) {
            data.setShotOccurred(false);
        }
    }
    private static void handleRemoveAttachment(RemoveAttachmentPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            AbstractContainerMenu menu = player.containerMenu;

            int slotId = payload.inventorySlot();

            if (slotId < 0 || slotId >= menu.slots.size())
                return;

            Slot slot = menu.getSlot(slotId);

            ItemStack gunStack = slot.getItem();

            if (!(gunStack.getItem() instanceof AbstractGunItem gun))
                return;

            gun.removeAttachment(gunStack, player, payload.attachmentSlot());
        });
    }
}
