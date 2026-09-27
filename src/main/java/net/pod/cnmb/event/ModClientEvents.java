package net.pod.cnmb.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.depleted_redstone.DepletedRedStoneBlock;
import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterBlockEntity;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.registry.ModBlocks;
import net.pod.cnmb.registry.ModDataComponents;

import java.util.List;

import static net.pod.cnmb.registry.ModGunAttachments.ATTACHMENTS;


@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class ModClientEvents {
    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, blockAndTintGetter, pos, tintIndex) -> {
            if (tintIndex == 0) {
                return RedStoneWireBlock.getColorForPower(state.getValue(DepletedRedStoneBlock.POWER));
            }

            return 0xFFFFFF;
        }, ModBlocks.DEPLETED_REDSTONE_BLOCK.get());
    }
    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((itemStack, tintIndex) -> {
            if (tintIndex == 0) {
                return RedStoneWireBlock.getColorForPower(itemStack.get(ModDataComponents.DEPLETED_REDSTONE_BLOCK_POWER.get()));
            }

            return 0xFFFFFF;
        }, ModBlocks.DEPLETED_REDSTONE_BLOCK.get().asItem());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RedStoneDepleterBlockEntity.registerRenderer(event);
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

