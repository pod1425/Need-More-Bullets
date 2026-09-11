package net.pod.cnmb.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.registry.ModDataComponents;

import java.util.List;

import static net.pod.cnmb.registry.ModDataComponents.SELECTED_ATTACHMENT;
import static net.pod.cnmb.registry.ModGunAttachments.ATTACHMENTS;


@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class ModClientEvents {


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

