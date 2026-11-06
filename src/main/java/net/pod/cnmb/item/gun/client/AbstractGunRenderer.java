package net.pod.cnmb.item.gun.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.event.ModClientEvents;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.utils.ArmTransform;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public abstract class AbstractGunRenderer<I extends AbstractGunItem> extends GeoItemRenderer<I> {
    public <M extends AbstractGunModel<I>> AbstractGunRenderer(M model) {
        super(model);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack ps, MultiBufferSource bs, int packedLight, int packedOverlay) {
        int d = 1;
        switch (context) {
            case ItemDisplayContext.FIRST_PERSON_LEFT_HAND:
                d = -1;
            case ItemDisplayContext.FIRST_PERSON_RIGHT_HAND:
                GeoBone leftArm = getGeoModel().getAnimationProcessor().getBone("left_arm");
                GeoBone rightArm = getGeoModel().getAnimationProcessor().getBone("right_arm");

                if (d == -1) {
                    GeoBone l = leftArm;
                    leftArm = rightArm;
                    rightArm = l;
                }

                if (rightArm != null) ModClientEvents.rightArm =
                        new ArmTransform(rightArm.getPosX() * d, rightArm.getPosY(), rightArm.getPosZ(),
                                rightArm.getRotX(), rightArm.getRotY() * d, rightArm.getRotZ() * d);
                if (leftArm != null) ModClientEvents.leftArm =
                        new ArmTransform(leftArm.getPosX() * d, leftArm.getPosY(), leftArm.getPosZ(),
                                leftArm.getRotX(), leftArm.getRotY() * d, leftArm.getRotZ() * d);

                ps.pushPose();
                ps.translate(0, -2.8F / 16, -8F / 16);
                super.renderByItem(stack, context, ps, bs, packedLight, packedOverlay);
                ps.popPose();
                break;
            default: super.renderByItem(stack, context, ps, bs, packedLight, packedOverlay);
        }
    }
}