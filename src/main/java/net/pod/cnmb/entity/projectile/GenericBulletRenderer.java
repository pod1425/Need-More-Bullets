package net.pod.cnmb.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.pod.cnmb.NeedMoreBulletsMod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GenericBulletRenderer extends EntityRenderer<GenericBulletEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "textures/entity/generic_bullet.png"
            );

    public GenericBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    private void renderBulletPart(GenericBulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                                       MultiBufferSource buffer, int packedLight, float roll) {
        poseStack.pushPose();

        Vec3 direction = entity.getLookAngle();

        float yaw = (float) Math.toDegrees(
                Math.atan2(direction.z, direction.x)
        );

        float pitch = (float) Math.toDegrees(
                Math.atan2(
                        direction.y,
                        Math.sqrt(direction.x * direction.x + direction.z * direction.z)
                )
        );
        poseStack.mulPose(
                Axis.YP.rotationDegrees(-yaw)
        );
        poseStack.mulPose(
                Axis.ZP.rotationDegrees(pitch)
        );
        poseStack.mulPose(
                Axis.XP.rotationDegrees(roll)
        );
        Matrix4f pose = poseStack.last().pose();

        float width = 0.5F;
        float length = 0.5F;
        VertexConsumer consumer =
                buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        consumer.addVertex(pose, -length / 2, -width / 2, 0)
                .setColor(255, 255, 255, 255)
                .setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(poseStack.last(), 0, 0, 1);

        consumer.addVertex(pose, length / 2, -width / 2, 0)
                .setColor(255, 255, 255, 255)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(poseStack.last(), 0, 0, 1);

        consumer.addVertex(pose, length / 2, width / 2, 0)
                .setColor(255, 255, 255, 255)
                .setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(poseStack.last(), 0, 0, 1);

        consumer.addVertex(pose, -length / 2, width / 2, 0)
                .setColor(255, 255, 255, 255)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(poseStack.last(), 0, 0, 1);

        poseStack.popPose();

    }

    @Override
    public void render(GenericBulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {

        renderBulletPart(entity, entityYaw, partialTick, poseStack, buffer, packedLight, 45);
        renderBulletPart(entity, entityYaw, partialTick, poseStack, buffer, packedLight, 135);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
    /*
    Override this method to change a texture in child class
     */
    @Override
    public ResourceLocation getTextureLocation(GenericBulletEntity entity) {
        return TEXTURE;
    }
}