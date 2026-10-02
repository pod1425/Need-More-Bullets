package net.pod.cnmb.entity.lead_golem.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LeadGolemRenderer extends GeoEntityRenderer<LeadGolem> {
    public LeadGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new LeadGolemModel());
        addRenderLayer(new LeadGolemEyesLayer(this));
    }
}
