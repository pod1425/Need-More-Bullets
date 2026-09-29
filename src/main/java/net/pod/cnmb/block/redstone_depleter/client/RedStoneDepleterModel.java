package net.pod.cnmb.block.redstone_depleter.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class RedStoneDepleterModel<T extends GeoAnimatable> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return NeedMoreBulletsMod.asResource("geo/redstone_depleter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return NeedMoreBulletsMod.asResource("textures/block/redstone_depleter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return null;
    }
}
