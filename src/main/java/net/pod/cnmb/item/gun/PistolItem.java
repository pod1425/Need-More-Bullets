package net.pod.cnmb.item.gun;

import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.item.gun.attachment.GunAttachmentsComponent;

import java.util.List;

public class PistolItem extends AbstractGunItem {
    public PistolItem(Properties properties, int shootRate, double bulletDamage,
                      double bulletSpeed, double inaccuracy, boolean isAutomatic) {
        super(properties, shootRate, bulletDamage, bulletSpeed, inaccuracy, isAutomatic, List.of(
                GunAttachmentSlot.SCOPE, GunAttachmentSlot.BARREL, GunAttachmentSlot.HANDLE,
                GunAttachmentSlot.MAGAZINE, GunAttachmentSlot.UNDERSIDE, GunAttachmentSlot.MISC1, GunAttachmentSlot.MISC2
        ));
    }
}
