package net.pod.cnmb.registry.custom;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.item.gun.attachment.GunAttachment;

public final class SimpleGunAttachmentHolder {

    private final ResourceLocation id;
    private GunAttachment value;

    SimpleGunAttachmentHolder(ResourceLocation id) {
        this.id = id;
    }


    public ResourceLocation id() {
        return id;
    }


    public GunAttachment get() {
        if (value == null) {
            throw new IllegalStateException(
                    "Gun attachment has not been initialized: " + id
            );
        }

        return value;
    }


    void bind(GunAttachment value) {
        if (this.value != null) {
            throw new IllegalStateException(
                    "Gun attachment has already been initialized: " + id
            );
        }

        this.value = value;
    }
}
