package net.pod.cnmb.entity.leadgolem.Client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.leadgolem.LeadGolem;

public class LeadGolemRender extends MobRenderer<LeadGolem, LeadGolemModel<LeadGolem>> {
    private final ItemRenderer itemRenderer;

    public LeadGolemRender(EntityRendererProvider.Context context) {
        super(context, new LeadGolemModel<>(context.bakeLayer(LeadGolemModel.LAYER_LOCATION)), 0.5f);

        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public ResourceLocation getTextureLocation(LeadGolem leadGolem) {
        return ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "textures/entity/leadgolem/lead_golem.png");
    }

    @Override
    public void render(LeadGolem entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        if (!entity.getWeapon().isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0, 0.8, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.yBodyRot));

            this.model.translateToWeapon(poseStack);

            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            poseStack.translate(0.1, 0.4, -0.9);

            itemRenderer.renderStatic(
                entity.getWeapon(),
                ItemDisplayContext.NONE,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                0);

            poseStack.popPose();
        }
    }
}
