package net.pod.cnmb.entity.lead_golem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class LeadGolemClothesLayer extends GeoRenderLayer<LeadGolem> {
    public enum Clothes {
        MAID("textures/entity/lead_golem/lead_golem_maid.png"),
        HELMET("textures/entity/lead_golem/lead_golem_helmet.png");

        public final ResourceLocation resource;

        Clothes(String path) {
            resource = NeedMoreBulletsMod.asResource(path);
        }
    }

    public LeadGolemClothesLayer(GeoRenderer<LeadGolem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack ps, LeadGolem lg, BakedGeoModel bm, @Nullable RenderType rt, MultiBufferSource bs,
                       @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (lg.clothes == null) return;
        RenderType crt = RenderType.entityCutoutNoCull(lg.clothes.resource);
        getRenderer().reRender(bm, ps, bs, lg, crt, bs.getBuffer(crt), partialTick, packedLight, packedOverlay, 0xFFFFFFFF);
    }
}
