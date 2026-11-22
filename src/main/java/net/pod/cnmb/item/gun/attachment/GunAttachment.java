package net.pod.cnmb.item.gun.attachment;

import com.google.common.collect.ImmutableList;
import net.pod.cnmb.event.gun.GunAttachmentEventType;
import net.pod.cnmb.event.gun.GunEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class GunAttachment implements Iterable<Consumer<GunEvent>> {
    // bitmask for O(1) lookup
    private final GunAttachmentSlot[] slots;
    // actual list of compatible slots
    private final ImmutableList<GunAttachmentSlot> usableSlots;
    private final Map<GunAttachmentEventType, Consumer<GunEvent>> handlers;

    private static final EnumMap<GunAttachmentEventType, Consumer<GunEvent>> EMPTY_HANDLER_MAP
            = new EnumMap<>(GunAttachmentEventType.class);

    private GunAttachment(List<GunAttachmentSlot> slots,
                          EnumMap<GunAttachmentEventType, Consumer<GunEvent>> handlers) {
        this.usableSlots = ImmutableList.copyOf(slots.stream().sorted().toList());
        this.slots = Arrays.stream(GunAttachmentSlot.values())
                .map(v -> usableSlots.contains(v) ? v : null)
                .toArray(GunAttachmentSlot[]::new);
        this.handlers = new EnumMap<>(handlers);
    }

    private GunAttachment(List<GunAttachmentSlot> slots) {
        this(slots, EMPTY_HANDLER_MAP);
    }

    public static GunAttachment createCustom(List<GunAttachmentSlot> slots) {
        return new GunAttachment(slots);
    }
    public static GunAttachment createScope() {
        return new GunAttachment(List.of(GunAttachmentSlot.SCOPE));
    }
    public static GunAttachment createBarrel() {
        return new GunAttachment(List.of(GunAttachmentSlot.BARREL));
    }
    public static GunAttachment createMagazine() {
        return new GunAttachment(List.of(GunAttachmentSlot.MAGAZINE));
    }
    public static GunAttachment createTrigger() {
        return new GunAttachment(List.of(GunAttachmentSlot.TRIGGER));
    }
    public static GunAttachment createHandle() {
        return new GunAttachment(List.of(GunAttachmentSlot.HANDLE));
    }
    public static GunAttachment createUnderside() {
        return new GunAttachment(List.of(GunAttachmentSlot.UNDERSIDE));
    }

    public GunAttachment addHandler(@NotNull GunAttachmentEventType type, @NotNull Consumer<GunEvent> handler) {
        handlers.put(type, handler);
        return this;
    }

    public GunAttachment onGunUnequip(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_GUN_UNEQUIP, handler);
        return this;
    }
    public GunAttachment onGunEquip(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_GUN_EQUIP, handler);
        return this;
    }
    public GunAttachment onShoot(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_SHOOT, handler);
        return this;
    }
    public GunAttachment onAim(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_AIM, handler);
        return this;
    }
    public GunAttachment onReload(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_RELOAD, handler);
        return this;
    }
    public GunAttachment onDamageReceived(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_GUN_EQUIP, handler);
        return this;
    }
    public GunAttachment onDamageDealt(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_GUN_EQUIP, handler);
        return this;
    }
    public GunAttachment onEntityHit(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_GUN_EQUIP, handler);
        return this;
    }
    public GunAttachment onBlockHit(@NotNull Consumer<GunEvent> handler) {
        addHandler(GunAttachmentEventType.ON_GUN_EQUIP, handler);
        return this;
    }





    public ImmutableList<GunAttachmentSlot> compatibleSlots() {
        return usableSlots;
    }

    public boolean isCompatible(GunAttachmentSlot slot) {
        return slots[slot.ordinal()] != null;
    }

    public void fire(GunAttachmentEventType event, GunEvent args) {
        Consumer<GunEvent> handler = handlers.get(event);
        if (handler != null) {
            handler.accept(args);
        }
    }

    @Override
    public @NotNull Iterator<Consumer<GunEvent>> iterator() {
        return handlers.values().iterator();
    }
}
