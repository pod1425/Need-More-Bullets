package net.pod.cnmb.item.gun.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import software.bernie.geckolib.model.GeoModel;

public class AbstractGunModel<T extends AbstractGunItem> extends GeoModel<T> {
    private final ResourceLocation model, texture, animation;
    public AbstractGunModel(String name) {
        model = NeedMoreBulletsMod.asResource("geo/" + name + ".geo.json");
        texture = NeedMoreBulletsMod.asResource("textures/item/" + name + ".png");
        animation = NeedMoreBulletsMod.asResource("animations/" + name + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animation;
    }
}
