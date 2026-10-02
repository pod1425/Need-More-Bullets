package net.pod.cnmb.entity.lead_golem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class LeadGolemEyesLayer extends GeoRenderLayer<LeadGolem> {
    private static final ResourceLocation EYES = NeedMoreBulletsMod.asResource("textures/entity/lead_golem/lead_golem_eyes.png");

    public LeadGolemEyesLayer(GeoRenderer<LeadGolem> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack ps, LeadGolem lg, BakedGeoModel bm, @Nullable RenderType rt, MultiBufferSource bs,
                       @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType eyes = RenderType.entityTranslucent(EYES);
        getRenderer().reRender(bm, ps, bs, lg, eyes, bs.getBuffer(eyes), partialTick, 0xF000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}