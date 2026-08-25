package net.pod.cnmb.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.pod.cnmb.NeedMoreBulletsMod;

public class ModTags {
    public static class Items {
        private static TagKey<Item> create(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, name));
        }

        public static final TagKey<Item> LEAD_GOLEM_EQUIPABLE = create("lead_golem_equipable");
    }
}
