package net.pod.cnmb.registry.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.attachment.GunAttachment;

import java.util.*;
import java.util.function.Supplier;

/**
Let me describe this class by quoting one of the great Blink 182 songs:
"Shit Piss Fuck Cunt Cocksucker Motherfucker Tits Fart Turd And Twat"
 */
public final class SimpleGunAttachmentRegistry {

    private final Map<ResourceLocation, SimpleGunAttachmentHolder> entries = new HashMap<>();
    private final List<PendingAttachment> pending = new ArrayList<>();
    private final Map<Item, SimpleGunAttachmentHolder> itemMappings = new HashMap<>();

    private boolean initialized = false;

    public SimpleGunAttachmentHolder register(String name, ItemLike item, Supplier<GunAttachment> factory) {
        ResourceLocation id =
                ResourceLocation.fromNamespaceAndPath(
                        NeedMoreBulletsMod.MODID,
                        name
                );

        if (entries.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Duplicate gun attachment: " + id
            );
        }

        SimpleGunAttachmentHolder holder =
                new SimpleGunAttachmentHolder(id);

        entries.put(id, holder);
        pending.add(new PendingAttachment(holder, item, factory));

        return holder;
    }


    public void initialize() {
        if (initialized) {
            throw new IllegalStateException(
                    "Gun attachments have already been initialized"
            );
        }

        for (PendingAttachment pendingAttachment : pending) {
            GunAttachment attachment =
                    pendingAttachment.factory().get();

            pendingAttachment.holder().bind(attachment);

            itemMappings.put(
                    pendingAttachment.item().asItem(),
                    pendingAttachment.holder()
            );
        }

        pending.clear();
        initialized = true;
    }


    public SimpleGunAttachmentHolder getHolder(ResourceLocation id) {
        return entries.get(id);
    }


    public GunAttachment get(ResourceLocation id) {
        SimpleGunAttachmentHolder holder = entries.get(id);
        return holder == null ? null : holder.get();
    }


    public SimpleGunAttachmentHolder getForItem(Item item) {
        return itemMappings.get(item);
    }


    public GunAttachment getForItemOrNull(Item item) {
        SimpleGunAttachmentHolder holder = itemMappings.get(item);
        return holder == null ? null : holder.get();
    }


    private record PendingAttachment(
            SimpleGunAttachmentHolder holder,
            ItemLike item,
            Supplier<GunAttachment> factory
    ) {}
}