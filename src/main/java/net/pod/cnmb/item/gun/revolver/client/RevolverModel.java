package net.pod.cnmb.item.gun.revolver.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.revolver.RevolverItem;
import software.bernie.geckolib.model.GeoModel;

public class RevolverModel extends GeoModel<RevolverItem> {
    @Override
    public ResourceLocation getModelResource(RevolverItem animatable) {
        return NeedMoreBulletsMod.asResource("geo/revolver.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RevolverItem animatable) {
        return NeedMoreBulletsMod.asResource("textures/item/revolver.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RevolverItem animatable) {
        return NeedMoreBulletsMod.asResource("animations/revolver.animation.json");
    }
}