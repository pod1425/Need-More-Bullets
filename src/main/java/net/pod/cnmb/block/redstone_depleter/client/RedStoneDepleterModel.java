package net.pod.cnmb.block.redstone_depleter.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterBlockEntity;
import net.pod.cnmb.registry.ModDataComponents;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class RedStoneDepleterModel<T extends GeoAnimatable> extends GeoModel<T> {
    private static final ResourceLocation CHARGED = NeedMoreBulletsMod.asResource("geo/redstone_depleter_charged.geo.json");
    private static final ResourceLocation SOURCE = NeedMoreBulletsMod.asResource("geo/redstone_depleter_source.geo.json");

    @Override
    public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        if (animatable instanceof RedStoneDepleterBlockEntity be) return be.getIsChargingMode() ? CHARGED : SOURCE;
        return ((GeoItemRenderer) renderer).getCurrentItemStack().getOrDefault(ModDataComponents.REDSTONE_DEPLETER_IS_CHARGING_MODE, true) ? CHARGED : SOURCE;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return CHARGED;
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
