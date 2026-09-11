package net.pod.cnmb.item.gun.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Reminder: Every operation on GunAttachmentsComponent should produce a new component rather than mutating the existing one.
 */
public class GunAttachmentsComponent {
    private final EnumMap<GunAttachmentSlot, ItemStack> attachments;

    private GunAttachmentsComponent(EnumMap<GunAttachmentSlot, ItemStack> attachments) {
        this.attachments = attachments;
    }

    public static GunAttachmentsComponent create(List<GunAttachmentSlot> slots) {
        EnumMap<GunAttachmentSlot, ItemStack> attachments =
                new EnumMap<>(GunAttachmentSlot.class);

        for (GunAttachmentSlot slot : slots) {
            attachments.put(slot, ItemStack.EMPTY);
        }

        return new GunAttachmentsComponent(attachments);
    }

    public ItemStack get(GunAttachmentSlot slot) {
        return attachments.getOrDefault(slot, ItemStack.EMPTY);
    }

    public boolean hasSlot(GunAttachmentSlot slot) {
        return attachments.containsKey(slot);
    }

    public GunAttachmentsComponent set(GunAttachmentSlot slot, ItemStack attachment) {
        if (!hasSlot(slot)) {
            throw new IllegalArgumentException(
                    "Gun does not have attachment slot: " + slot
            );
        }

        EnumMap<GunAttachmentSlot, ItemStack> copy =
                new EnumMap<>(attachments);

        copy.put(slot, attachment.copy());
        return new GunAttachmentsComponent(copy);
    }

    public GunAttachmentsComponent remove(GunAttachmentSlot slot) {
        if (!hasSlot(slot)) {
            throw new IllegalArgumentException(
                    "Gun does not have attachment slot: " + slot
            );
        }

        EnumMap<GunAttachmentSlot, ItemStack> copy =
                new EnumMap<>(attachments);

        copy.put(slot, ItemStack.EMPTY);

        return new GunAttachmentsComponent(copy);
    }

    public EnumMap<GunAttachmentSlot, ItemStack> attachments() {
        return new EnumMap<>(attachments);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof GunAttachmentsComponent other))
            return false;

        return attachments.equals(other.attachments);
    }

    @Override
    public int hashCode() {
        return attachments.hashCode();
    }

    public static final Codec<GunAttachmentsComponent> CODEC =
            Codec.unboundedMap(
                    Codec.STRING.xmap(
                            name -> GunAttachmentSlot.valueOf(
                                    name.toUpperCase(Locale.ROOT)
                            ),
                            slot -> slot.name().toLowerCase(Locale.ROOT)
                    ),
                    ItemStack.OPTIONAL_CODEC
            ).xmap(
                    map -> {
                        EnumMap<GunAttachmentSlot, ItemStack> attachments =
                                new EnumMap<>(GunAttachmentSlot.class);

                        attachments.putAll(map);

                        return new GunAttachmentsComponent(attachments);
                    },
                    GunAttachmentsComponent::attachments
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, GunAttachmentsComponent>
            STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GunAttachmentsComponent decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();

            EnumMap<GunAttachmentSlot, ItemStack> attachments = new EnumMap<>(GunAttachmentSlot.class);

            for (int i = 0; i < size; i++) {
                GunAttachmentSlot slot = buf.readEnum(GunAttachmentSlot.class);
                ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                attachments.put(slot, stack);
            }
            return new GunAttachmentsComponent(attachments);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, GunAttachmentsComponent component) {
            EnumMap<GunAttachmentSlot, ItemStack> attachments =
                    component.attachments();
            buf.writeVarInt(attachments.size());

            for (Map.Entry<GunAttachmentSlot, ItemStack> entry :
                    attachments.entrySet()) {

                buf.writeEnum(entry.getKey());
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, entry.getValue());
            }
        }
    };
}
