package net.pod.cnmb.registry;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.PistolItem;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeedMoreBulletsMod.MODID);
    // for Creative mode tab
    private static List<DeferredItem<Item>> items = new ArrayList<>();
    public static final DeferredItem<Item> LEAD_INGOT = add(ITEMS.register("lead_ingot",
            () -> new Item(new Item.Properties())));
    public static final DeferredItem<Item> LEAD_NUGGET = add(ITEMS.register("lead_nugget",
            () -> new Item(new Item.Properties())));
    public static final DeferredItem<Item> RAW_LEAD = add(ITEMS.register("raw_lead",
            () -> new Item(new Item.Properties())));
    public static final DeferredItem<Item> STEEL_INGOT = add(ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties())));
    public static final DeferredItem<Item> STEEL_NUGGET = add(ITEMS.register("steel_nugget",
            () -> new Item(new Item.Properties())));
    public static final DeferredItem<Item> PISTOL = add(ITEMS.register("pistol",
            () -> new PistolItem(new Item.Properties(), 4, 10, 8)));

    private static DeferredItem<Item> add(DeferredItem<Item> item) {
        items.add(item);
        return item;
    }

    public static List<Item> getItems() {
        return items.stream().map(DeferredHolder::get).toList();
    }
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
