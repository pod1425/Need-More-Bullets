package net.pod.cnmb.block.redstone_depleter.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterBlockEntity;

public class RedStoneDepleterRenderer implements BlockEntityRenderer<RedStoneDepleterBlockEntity> {
    public RedStoneDepleterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(RedStoneDepleterBlockEntity be, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        ItemStack stack = be.getItem();
        if (stack.isEmpty()) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel itemModel = itemRenderer.getModel(stack, null, null, 0);

        poseStack.pushPose();
        poseStack.translate(0.5D, 13.0D / 16.0D, 0.5D);
        poseStack.scale(0.4F, 0.4F, 0.4F);

        if (itemModel.isGui3d()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        } else {
            poseStack.translate(0, -2.5F / 16.0F, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));
        }

        itemRenderer.render(stack, ItemDisplayContext.FIXED, false, poseStack, multiBufferSource, i, i1, itemModel);

        poseStack.popPose();
    }
}
