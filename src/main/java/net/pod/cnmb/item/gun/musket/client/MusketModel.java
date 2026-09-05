package net.pod.cnmb.item.gun.musket.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.musket.MusketItem;
import software.bernie.geckolib.model.GeoModel;

public class MusketModel extends GeoModel<MusketItem> {
    @Override
    public ResourceLocation getModelResource(MusketItem animatable) {
        return NeedMoreBulletsMod.asResource("geo/musket.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MusketItem animatable) {
        return NeedMoreBulletsMod.asResource("textures/item/musket.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MusketItem animatable) {
        return NeedMoreBulletsMod.asResource("animations/musket.animation.json");
    }
}