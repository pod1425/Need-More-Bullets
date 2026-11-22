package net.pod.cnmb.event.gun;

public enum GunAttachmentEventType {
    ON_GUN_EQUIP(GunEquipEvent.class),
    ON_GUN_UNEQUIP(GunUnequipEvent.class),
    ON_SHOOT(GunShootEvent.class),
    ON_AIM(GunAimEvent.class),
    ON_RELOAD(GunReloadEvent.class),
    ON_DAMAGE_RECEIVED(GunHolderDamageReceivedEvent.class),
    ON_DAMAGE_DEALT(GunDealtDamageEvent.class),
    ON_ENTITY_HIT(GunBulletEntityHitEvent.class),
    ON_BLOCK_HIT(GunBulletBlockHitEvent.class);


    private final Class<? extends GunEvent> eventClass;

    GunAttachmentEventType(Class<? extends GunEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public <T extends GunEvent> T cast(GunEvent event) {
        return (T) eventClass.cast(event);
    }
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
