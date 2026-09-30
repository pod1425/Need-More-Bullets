package net.pod.cnmb.block.slip_roller.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class SlipRollerModel<T extends GeoAnimatable> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return NeedMoreBulletsMod.asResource("geo/slip_roller.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return NeedMoreBulletsMod.asResource("textures/block/slip_roller.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return null;
    }
}