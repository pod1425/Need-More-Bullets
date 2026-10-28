package net.pod.cnmb.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.GunPlayerData;

public class ModPlayerAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.ATTACHMENT_TYPES,
                    NeedMoreBulletsMod.MODID
            );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<GunPlayerData>> GUN_DATA =
            ATTACHMENTS.register(
                    "gun_data",
                    () -> AttachmentType.builder(GunPlayerData::new).build()
            );

    public static void register (IEventBus eventBus){
        ATTACHMENTS.register(eventBus);
    }
}
