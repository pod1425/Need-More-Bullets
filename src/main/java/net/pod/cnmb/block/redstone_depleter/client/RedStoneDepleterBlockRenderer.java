package net.pod.cnmb.block.redstone_depleter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterBlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.getRotationOffsetForPosition;

public class RedStoneDepleterBlockRenderer extends GeoBlockRenderer<RedStoneDepleterBlockEntity> {
    public RedStoneDepleterBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new RedStoneDepleterModel<>());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RedStoneDepleterBlockEntity be, BakedGeoModel model,
                               @Nullable RenderType rt, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        float angle = KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y);
        model.getBone("rotating_part").ifPresent(part -> {
            part.setRotY(angle);
            System.out.println("angle = " + angle + ", rot = " + part.getRotY());
            System.out.println(
                    "time=" + AnimationTickHolder.getRenderTime(be.getLevel()) +
                            ", speed=" + be.getSpeed() +
                            ", offset=" + getRotationOffsetForPosition(be, be.getBlockPos(), Direction.Axis.Y)
            );
        });

        if (buffer != null)
            super.actuallyRender(poseStack, animatable, model, rt, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        ItemStack stack = be.getItem();
        if (stack.isEmpty()) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel itemModel = itemRenderer.getModel(stack, null, null, 0);

        poseStack.pushPose();
        poseStack.translate(0.0D, 14.0D / 16.0D, 0.0D);
        poseStack.scale(0.4F, 0.4F, 0.4F);

        if (itemModel.isGui3d()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        } else {
            poseStack.translate(0, -2.5F / 16.0F, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));
        }

        itemRenderer.render(stack, ItemDisplayContext.FIXED, false, poseStack, bufferSource, packedLight, packedOverlay, itemModel);

        poseStack.popPose();
    }
}
