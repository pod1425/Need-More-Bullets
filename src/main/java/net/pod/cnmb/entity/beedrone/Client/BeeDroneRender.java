package net.pod.cnmb.entity.beedrone.Client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.beedrone.BeeDrone;

public class BeeDroneRender extends MobRenderer<BeeDrone, BeeDroneModel<BeeDrone>> {
    public BeeDroneRender(EntityRendererProvider.Context context) {
        super(context, new BeeDroneModel<>(context.bakeLayer(BeeDroneModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BeeDrone beeDrone) {
        return ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "textures/entity/bee_drone.png");
    }

    @Override
    public void render(BeeDrone entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
