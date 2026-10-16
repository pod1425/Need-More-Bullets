package net.pod.cnmb.block.pallet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.pod.cnmb.registry.ModBlockEntities;

public class PalletBlockRenderer extends SafeBlockEntityRenderer<PalletBlockEntity> {

    public PalletBlockRenderer(BlockEntityRendererProvider.Context context) {}

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.PALLET.get(),
                PalletBlockRenderer::new
        );
    }

    @Override
    protected void renderSafe(PalletBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var item = be.getHeldItem();
        if (item.isEmpty())
            return;

        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var bakedModel = itemRenderer.getModel(item, null, null, 0);
        var isUpright = BeltHelper.isItemUpright(item);
        var isBlockItem = bakedModel.isGui3d();
        var direction = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        ms.pushPose();


        if (isUpright || isBlockItem) {
            ms.translate(.5f, 0.85f, .5f);
            ms.mulPose(Axis.YP.rotationDegrees(direction.toYRot() + 180));
        } else {
            ms.translate(.5f, 0.52f, .5f);
            ms.mulPose(Axis.YP.rotationDegrees(direction.toYRot() + 180));
            ms.mulPose(Axis.XP.rotationDegrees(90));
        }

        if (isBlockItem) { // isBlockItem
            ms.scale(1.5f, 1.5f, 1.5f);
        } else {
            ms.scale(.75f, .75f, .75f);
        }

        itemRenderer.render(item, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
        ms.popPose();
    }
}
