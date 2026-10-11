package net.pod.cnmb.block.redstone_depleter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterBlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RedStoneDepleterBlockRenderer extends GeoBlockRenderer<RedStoneDepleterBlockEntity> {
    public RedStoneDepleterBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new RedStoneDepleterModel<>());
    }

    private static final ResourceLocation MODE2 = NeedMoreBulletsMod.asResource("textures/block/redstone_depleter_2.png");

    @Override
    public void actuallyRender(PoseStack poseStack, RedStoneDepleterBlockEntity be, BakedGeoModel model,
                               @Nullable RenderType rt, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        model.getBone("rotating_part").ifPresent(part ->
                part.setRotY(KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y)));

        if (!be.getIsChargingMode()) rt = RenderType.entityCutoutNoCull(MODE2);
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
