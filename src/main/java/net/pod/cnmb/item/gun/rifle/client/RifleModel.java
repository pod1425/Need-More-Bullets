package net.pod.cnmb.item.gun.rifle.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.rifle.RifleItem;
import software.bernie.geckolib.model.GeoModel;

public class RifleModel extends GeoModel<RifleItem> {
    @Override
    public ResourceLocation getModelResource(RifleItem animatable) {
        return NeedMoreBulletsMod.asResource("geo/rifle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RifleItem animatable) {
        return NeedMoreBulletsMod.asResource("textures/item/rifle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RifleItem animatable) {
        return NeedMoreBulletsMod.asResource("animations/rifle.animation.json");
    }
}