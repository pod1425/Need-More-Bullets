package net.pod.cnmb.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;

public record RemoveAttachmentPayload(
        int inventorySlot,
        int attachmentSlot
) implements CustomPacketPayload {
    public static final Type<RemoveAttachmentPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "remove_attachment"
            ));

    public static final StreamCodec<ByteBuf, RemoveAttachmentPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RemoveAttachmentPayload::inventorySlot,
                    ByteBufCodecs.VAR_INT,
                    RemoveAttachmentPayload::attachmentSlot,
                    RemoveAttachmentPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}