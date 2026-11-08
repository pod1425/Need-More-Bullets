package net.pod.cnmb.block.thermometer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;

public class ThermometerRenderer implements BlockEntityRenderer<ThermometerBlockEntity> {
    private static final ResourceLocation[] textures = new ResourceLocation[16];

    public ThermometerRenderer(BlockEntityRendererProvider.Context context) {
        for (int i = 0; i < 16; i++) textures[i] = NeedMoreBulletsMod.asResource("textures/thermometer/" + i + ".png");
    }

    @Override
    public void render(ThermometerBlockEntity be, float pt, PoseStack ps, MultiBufferSource bs, int packedLight, int packedOverlay) {

    }
}
