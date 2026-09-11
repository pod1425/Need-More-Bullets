package net.pod.cnmb.item.gun.attachment;

public enum GunAttachmentEventType {
    ON_GUN_EQUIP,
    ON_GUN_UNEQUIP,
    ON_SHOOT,
    ON_AIM,
    ON_RELOAD,
    ON_DAMAGE_RECEIVED,
    ON_DAMAGE_DEALT,
    ON_ENTITY_HIT,
    ON_BLOCK_HIT
}
/*
public static final DefferredHolder<GunAttachment> WITHERING_BARREL = ATTACHMENTS.register(
    "withering_barrel",
    ModItems.WITHERING_BARREL,
    () -> new GunAttachment()
    .onSlots(List.of(GunAttachmentType.BARREL))
    .addHandler(ON_ENTITY_HIT, args -> {
        Entity target = args.target();
        if (target == null) return;
        target.applyEffect(Effects.WITHER, 2, 20);
    }
 */
