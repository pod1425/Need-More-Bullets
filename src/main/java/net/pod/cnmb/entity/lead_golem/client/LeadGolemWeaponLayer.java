package net.pod.cnmb.entity.lead_golem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class LeadGolemWeaponLayer extends BlockAndItemGeoLayer<LeadGolem> {
    public LeadGolemWeaponLayer(GeoRenderer<LeadGolem> renderer) {
        super(renderer);
    }

    @Override
    protected @Nullable ItemStack getStackForBone(GeoBone bone, LeadGolem animatable) {
        return bone.getName().equals("weapon_anchor") ? animatable.getWeaponItem() : null;
    }

    @Override
    protected void renderStackForBone(PoseStack ps, GeoBone bone, ItemStack stack, LeadGolem animatable, MultiBufferSource bs,
                                      float partialTick, int packedLight, int packedOverlay) {
        ps.mulPose(Axis.XP.rotationDegrees(-90f));
        ps.translate(0f, -0.15f, -0.2f);
        super.renderStackForBone(ps, bone, stack, animatable, bs, partialTick, packedLight, packedOverlay);
    }
}
