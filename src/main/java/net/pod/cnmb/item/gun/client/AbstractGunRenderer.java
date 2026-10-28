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
        if (context.firstPerson()) {
            GeoBone leftArm = getGeoModel().getAnimationProcessor().getBone("left_arm");
            if (leftArm != null) ModClientEvents.leftArm =
                    new ArmTransform(leftArm.getPosX(), leftArm.getPosY(), leftArm.getPosZ(), leftArm.getRotX(), leftArm.getRotY(), leftArm.getRotZ());

            GeoBone rightArm = getGeoModel().getAnimationProcessor().getBone("right_arm");
            if (rightArm != null) ModClientEvents.rightArm =
                    new ArmTransform(rightArm.getPosX(), rightArm.getPosY(), rightArm.getPosZ(), rightArm.getRotX(), rightArm.getRotY(), rightArm.getRotZ());

            ps.pushPose();
            ps.translate(0, -2F / 16, -8F / 16);
            super.renderByItem(stack, context, ps, bs, packedLight, packedOverlay);
            ps.popPose();
        } else  super.renderByItem(stack, context, ps, bs, packedLight, packedOverlay);
    }
}