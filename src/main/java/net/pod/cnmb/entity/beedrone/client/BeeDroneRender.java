package net.pod.cnmb.entity.beedrone.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.entity.beedrone.BeeDrone;

public class BeeDroneRender extends MobRenderer<BeeDrone, BeeDroneModel<BeeDrone>> {
    public BeeDroneRender(EntityRendererProvider.Context context) {
        super(context, new BeeDroneModel<>(context.bakeLayer(BeeDroneModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BeeDrone beeDrone) {
        return BeeDroneTextures.MAIN.res;
    }
}