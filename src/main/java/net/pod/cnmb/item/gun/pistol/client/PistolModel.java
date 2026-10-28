package net.pod.cnmb.item.gun.pistol.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.pistol.PistolItem;
import software.bernie.geckolib.model.GeoModel;

public class PistolModel extends GeoModel<PistolItem> {
    @Override
    public ResourceLocation getModelResource(PistolItem animatable) {
        return NeedMoreBulletsMod.asResource("geo/pistol.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PistolItem animatable) {
        return NeedMoreBulletsMod.asResource("textures/item/pistol.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PistolItem animatable) {
        return NeedMoreBulletsMod.asResource("animations/pistol.animation.json");
    }
}