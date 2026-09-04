package net.pod.cnmb.item.gun.Client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.PistolItem;
import software.bernie.geckolib.model.GeoModel;

public class PistolItemModel extends GeoModel<PistolItem>{

    @Override
    public ResourceLocation getModelResource(PistolItem animatable) {
        return NeedMoreBulletsMod.asResource("geo/musket.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PistolItem animatable) {
        return NeedMoreBulletsMod.asResource("textures/item/pistol.png");    }

    @Override
    public ResourceLocation getAnimationResource(PistolItem animatable) {
        return NeedMoreBulletsMod.asResource("animations/musket_fire.json");    }
}
