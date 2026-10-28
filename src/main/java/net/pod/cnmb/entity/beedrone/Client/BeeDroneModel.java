package net.pod.cnmb.entity.beedrone.Client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.beedrone.BeeDrone;

public class BeeDroneModel<T extends BeeDrone> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "beedrone"), "main");
    private final ModelPart drone;
    private final ModelPart fan_right;
    private final ModelPart fan_left;
    private final ModelPart tail;

    public BeeDroneModel(ModelPart root) {
        this.drone = root.getChild("drone");
        this.fan_right = this.drone.getChild("fan_right");
        this.fan_left = this.drone.getChild("fan_left");
        this.tail = this.drone.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition drone = partdefinition.addOrReplaceChild("drone", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -9.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(24, 16).addBox(-1.5F, -9.0F, -8.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 14).addBox(1.5F, -9.0F, -8.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition fan_right = drone.addOrReplaceChild("fan_right", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.75F, -9.25F, 0.0F));

        PartDefinition fan_left = drone.addOrReplaceChild("fan_left", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(3.75F, -9.25F, 0.0F));

        PartDefinition tail = drone.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(23, 18).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.5F, 5.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.drone.xRot = headPitch * ((float)Math.PI / 180f);

        this.animate(entity.flyAnimationState, BeeDroneAnimations.bee_drone_fly, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        drone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return drone;
    }
}
