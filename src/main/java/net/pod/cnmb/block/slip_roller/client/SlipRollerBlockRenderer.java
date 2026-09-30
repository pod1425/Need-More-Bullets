package net.pod.cnmb.block.slip_roller.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.pod.cnmb.block.slip_roller.SlipRollerBlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SlipRollerBlockRenderer extends GeoBlockRenderer<SlipRollerBlockEntity> {
    public SlipRollerBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new SlipRollerModel<>());
    }

    @Override
    public void actuallyRender(PoseStack ps, SlipRollerBlockEntity be, BakedGeoModel model, @Nullable RenderType rt,
                               MultiBufferSource bs, @Nullable VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, int colour) {
        float angle = (be.getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING)
                .getClockWise().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? -1f : 1f) *
                KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), KineticBlockEntityRenderer.getRotationAxisOf(be));

        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float rollAngle = ((time * Math.abs(be.getSpeed()) * 3f / 10) % 360) / 180 * (float) Math.PI;

        model.getBone("shaft").ifPresent(bone -> bone.setRotX(angle));
        model.getBone("rolling_shaft_1").ifPresent(bone -> bone.setRotZ(-rollAngle));
        model.getBone("rolling_shaft_2").ifPresent(bone -> bone.setRotZ(rollAngle));

        super.actuallyRender(ps, animatable, model, rt, bs, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}