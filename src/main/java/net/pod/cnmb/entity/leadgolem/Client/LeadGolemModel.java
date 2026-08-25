package net.pod.cnmb.entity.leadgolem.Client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.leadgolem.LeadGolem;

public class LeadGolemModel<T extends LeadGolem> extends HierarchicalModel<T> {

         public static final ModelLayerLocation LAYER_LOCATION =
                new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "leadgolem"), "main");
        private final ModelPart tilo;
        private final ModelPart holova;
        private final ModelPart ruka_prava;
//        private final ModelPart ruka_liva;
//        private final ModelPart noha_liva;
//        private final ModelPart noha_prava;

        public LeadGolemModel (ModelPart root) {
            this.tilo = root.getChild("tilo");
            this.holova = this.tilo.getChild("holova");
            this.ruka_prava = this.tilo.getChild("ruka_prava");
//            this.ruka_liva = this.tilo.getChild("ruka_liva");
//            this.noha_liva = root.getChild("noha_liva");
//            this.noha_prava = root.getChild("noha_prava");
        }

        public void translateToWeapon(PoseStack poseStack) {
            this.tilo.translateAndRotate(poseStack);
            this.ruka_prava.translateAndRotate(poseStack);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition tilo = partdefinition.addOrReplaceChild("tilo", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(16, 32).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 12.0F, 0.0F));

            PartDefinition holova = tilo.addOrReplaceChild("holova", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(32, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -12.0F, -2.0F));

            PartDefinition ruka_prava = tilo.addOrReplaceChild("ruka_prava", CubeListBuilder.create().texOffs(40, 16).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(40, 32).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-4.0F, -6.0F, 0.0F));

            PartDefinition ruka_liva = tilo.addOrReplaceChild("ruka_liva", CubeListBuilder.create().texOffs(32, 48).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(48, 48).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(4.0F, -6.0F, 0.0F));

            PartDefinition noha_liva = tilo.addOrReplaceChild("noha_liva", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(2.0F, 0.0F, 0.0F));

            PartDefinition noha_prava = tilo.addOrReplaceChild("noha_prava", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(LeadGolem entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            this.applyHeadRotation(netHeadYaw, headPitch);

            this.animateWalk(entity.getWeapon().isEmpty() ? LeadGolemAnimations.lead_golem_walk : LeadGolemAnimations.lead_golem_walk_armed, limbSwing, limbSwingAmount, 2f, 2.5f);
            //this.animate(entity.idleAnimationState, LeadGolemAnimations.lead_golem_walk, ageInTicks, 1f);//idle
        }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.holova.yRot = headYaw * ((float)Math.PI / 180f);
        this.holova.xRot = headPitch *  ((float)Math.PI / 180f);
    }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
            tilo.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            //noha_liva.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            //noha_prava.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }

    @Override
    public ModelPart root() {
        return tilo;
    }
}
