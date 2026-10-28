package net.pod.cnmb.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Options;
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
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.networking.payload.ControlInputPayload;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.networking.payload.StopControlPayload;
import net.pod.cnmb.registry.ModKeyBinds;
import net.pod.cnmb.util.ControllableMob;

import java.util.List;

import static net.pod.cnmb.registry.ModGunAttachments.ATTACHMENTS;


@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyBinds.register(event);
    }

    public static ControllableMob controlling;
    public static double pitch, yaw;
    private static boolean prevActiveCustomAttack;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (controlling != null) {
            Options opt = Minecraft.getInstance().options;

            ModNetworking.sendControlInput(new ControlInputPayload((float) pitch, (float) yaw,
                    opt.keyUp.isDown(), opt.keyDown.isDown(),
                    opt.keyLeft.isDown(), opt.keyRight.isDown(),
                    opt.keyJump.isDown(), opt.keyShift.isDown()));

            if (ModKeyBinds.STOP_CONTROL.consumeClick()) ModNetworking.sendStopControl(new StopControlPayload(controlling.getId()));
            if (ModKeyBinds.ACTIVATE_CUSTOM_ATTACK.isDown()) {
                if (!prevActiveCustomAttack) {
                    ModNetworking.sendCustomAttack(controlling.getId());
                    prevActiveCustomAttack = true;
                }
            } else {
                prevActiveCustomAttack = false;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (controlling != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (controlling != null && event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            event.setCanceled(true);
        }
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

