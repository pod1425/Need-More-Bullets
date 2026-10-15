package net.pod.cnmb.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;

import java.util.List;

import static net.pod.cnmb.registry.ModGunAttachments.ATTACHMENTS;


@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class ModClientEvents {
    private static final ResourceLocation GUN_CROSSHAIR = NeedMoreBulletsMod.asResource("textures/gui/gun_crosshair.png");

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

