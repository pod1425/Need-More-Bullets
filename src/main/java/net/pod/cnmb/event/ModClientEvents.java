package net.pod.cnmb.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.networking.payload.StartGunReloadAnimationPayload;
import net.pod.cnmb.registry.ModKeyBinds;
import net.pod.cnmb.utils.ArmTransform;

import java.util.List;

import static net.pod.cnmb.registry.ModGunAttachments.ATTACHMENTS;


@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class ModClientEvents {
    private static final ResourceLocation GUN_CROSSHAIR = NeedMoreBulletsMod.asResource("textures/gui/gun_crosshair.png");
    public static ArmTransform leftArm = ArmTransform.ZERO;
    public static ArmTransform rightArm = ArmTransform.ZERO;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (ModKeyBinds.RELOAD.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            Player p = mc.player;
            if (p == null) return;

            if (p.getMainHandItem().getItem() instanceof AbstractGunItem) ModNetworking.sendToServer(new StartGunReloadAnimationPayload(true));
            else if (p.getOffhandItem().getItem() instanceof AbstractGunItem) ModNetworking.sendToServer(new StartGunReloadAnimationPayload(false));
        }
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyBinds.register(event);
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && (mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof AbstractGunItem ||
                mc.player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof AbstractGunItem)) {
            event.setCanceled(true);
            if (mc.screen == null && !mc.options.hideGui) {
                int size = 9;
                int x = (mc.getWindow().getGuiScaledWidth() - size) / 2;
                int y = (mc.getWindow().getGuiScaledHeight() - size) / 2;

                event.getGuiGraphics().blit(GUN_CROSSHAIR, x, y, 0, 0, size, size, size, size);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer p = mc.player;
        if (p == null) return;
        if (!(p.getMainHandItem().getItem() instanceof AbstractGunItem)) return;

        PoseStack ps = event.getPoseStack();

        PlayerRenderer r = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(p);
        MultiBufferSource bs = event.getMultiBufferSource();
        int light = event.getPackedLight();

        ps.pushPose();
        getHandPoseStack(ps, HumanoidArm.LEFT, r);
        r.renderLeftHand(ps, bs, light, p);
        ps.popPose();

        ps.pushPose();
        getHandPoseStack(ps, HumanoidArm.RIGHT, r);
        r.renderRightHand(ps, bs, light, p);
        ps.popPose();
    }

    static void getHandPoseStack(PoseStack ps, HumanoidArm arm, PlayerRenderer r) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

        ps.translate(f * 0.2F, -0.5F, -0.5F);
        ps.mulPose(Axis.YP.rotationDegrees(f * 180.0F));
        ps.mulPose(Axis.XP.rotationDegrees(90.0F));
        ps.mulPose(Axis.ZN.rotationDegrees(f * 5.0F));

        ArmTransform armTransform = arm == HumanoidArm.RIGHT ? rightArm : leftArm;
        ModelPart armPart = arm == HumanoidArm.RIGHT ? r.getModel().rightArm : r.getModel().leftArm;

        float px = armPart.x / 16.0F;
        float py = (armPart.y + 1.5F) / 16.0F;
        float pz = armPart.z / 16.0F;

        ps.translate(px, py, pz);

        ps.translate(
                armTransform.x() / 16.0,
                -armTransform.z() / 16.0,
                -armTransform.y() / 16.0
        );

        ps.mulPose(Axis.XN.rotation(armTransform.rotX()));
        ps.mulPose(Axis.YN.rotation(armTransform.rotZ()));
        ps.mulPose(Axis.ZN.rotation(armTransform.rotY()));

        ps.translate(-px, -py, -pz);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        GunAttachment attachment =
                ATTACHMENTS.getForItemOrNull(stack.getItem());

        if (attachment == null)
            return;

        List<Component> tooltip = event.getToolTip();

        tooltip.add(
                Component.translatable("attachment.cnmb.can_be_attached_text")
                        .withStyle(ChatFormatting.GRAY)
        );

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.empty());

            tooltip.add(
                    Component.translatable("attachment.cnmb.compatible_slots_text")
                            .withStyle(ChatFormatting.GRAY)
            );

            for (GunAttachmentSlot slot : attachment.compatibleSlots()) {
                tooltip.add(
                        Component.translatable(slot.getTranslationKey())
                                .withStyle(ChatFormatting.DARK_GRAY)
                );
            }
        } else {
            tooltip.add(
                    Component.translatable("attachment.cnmb.hold_shift_text")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }
}

