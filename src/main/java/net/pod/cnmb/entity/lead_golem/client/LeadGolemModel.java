package net.pod.cnmb.entity.lead_golem.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import software.bernie.geckolib.model.GeoModel;

public class LeadGolemModel extends GeoModel<LeadGolem> {
    @Override
    public ResourceLocation getModelResource(LeadGolem animatable) {
        return NeedMoreBulletsMod.asResource("geo/lead_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LeadGolem animatable) {
        return NeedMoreBulletsMod.asResource("textures/entity/lead_golem/lead_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LeadGolem animatable) {
        return NeedMoreBulletsMod.asResource("animations/lead_golem.animation.json");
    }
}
