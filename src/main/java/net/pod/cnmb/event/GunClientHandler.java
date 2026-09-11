package net.pod.cnmb.event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachmentsComponent;
import net.pod.cnmb.networking.EquipAttachmentPayload;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.networking.RemoveAttachmentPayload;
import net.pod.cnmb.registry.ModDataComponents;
import net.pod.cnmb.registry.ModGunAttachments;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static net.pod.cnmb.registry.ModDataComponents.IS_AUTOMATIC;
import static net.pod.cnmb.registry.ModDataComponents.SELECTED_ATTACHMENT;

@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class GunClientHandler {
    // This should become a player capability, probably.
    // If it doesn't take too much processing time, that is.
    private static boolean wasPressed = false;
    private static boolean removingAttachment;


    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (!(mc.screen instanceof AbstractContainerScreen<?> screen))
            return;

        double scroll = event.getScrollDeltaY();
        if (scroll == 0)
            return;
        Slot slot = screen.getSlotUnderMouse();

        if (slot != null && slot.getItem().getItem() instanceof AbstractGunItem) {
            ItemStack stack = slot.getItem();
            int slotCount = stack.get(ModDataComponents.GUN_ATTACHMENTS.get()).attachments().size();
            int currentSlot = stack.get(SELECTED_ATTACHMENT.get());
            if (scroll > 0) {
                // scroll up
                if (currentSlot > 0) currentSlot--;
                else currentSlot = slotCount - 1;
            } else {
                // scroll down
                if (currentSlot < slotCount - 1) currentSlot++;
                else currentSlot = 0;
            }
            stack.set(SELECTED_ATTACHMENT, currentSlot);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onInteractionKey(
            InputEvent.InteractionKeyMappingTriggered event) {
        // ignore everything thats not attack
        if (!event.isAttack()) {
            return;
        }
        // Minecraft Minecraft Minecraft Minecraft Minecraft Minecraft Minecraft Minecraft Minecraft Minecraft
        // i love Java!
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        // dont do anything if main or off hand item is not a gun
        if (!playerHoldsGun(minecraft.player)) {
            return;
        }
        // cancel vanilla attack processing (to not hit blocks)
        event.setCanceled(true);
        // prevent the hand swing animation
        event.setSwingHand(false);
    }
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            wasPressed = false;
            return;
        }

        boolean pressed = mc.options.keyAttack.isDown();

        if (pressed != wasPressed) {
            wasPressed = pressed;

            ItemStack mainHand = mc.player.getMainHandItem();
            ItemStack offHand = mc.player.getOffhandItem();

            boolean mainGun = mainHand.getItem() instanceof AbstractGunItem;
            boolean offGun = offHand.getItem() instanceof AbstractGunItem;

            if (mainGun && offGun) {
                PacketDistributor.sendToServer(
                        new ModNetworking.GunTriggerPayload(
                                pressed,
                                ModNetworking.GunHand.BOTH
                        )
                );
            } else if (mainGun) {
                PacketDistributor.sendToServer(
                        new ModNetworking.GunTriggerPayload(
                                pressed,
                                ModNetworking.GunHand.MAIN_HAND
                        )
                );
            } else if (offGun) {
                PacketDistributor.sendToServer(
                        new ModNetworking.GunTriggerPayload(
                                pressed,
                                ModNetworking.GunHand.OFF_HAND
                        )
                );
            }
        }
    }
    @SubscribeEvent
    public static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
        removingAttachment = false;
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen))
            return;

        Slot slot = screen.getSlotUnderMouse();
        if (slot == null)
            return;
        ItemStack gunStack = slot.getItem();

        if (!(gunStack.getItem() instanceof AbstractGunItem))
            return;
        // Ctrl + Right Click = remove attachment
        if (Screen.hasControlDown() && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int selected = gunStack.get(ModDataComponents.SELECTED_ATTACHMENT.get());

            removingAttachment = true;
            event.setCanceled(true);

            PacketDistributor.sendToServer(
                    new RemoveAttachmentPayload(slot.index, selected)
            );
            return;
        }
        // Left Click with an attachment on the cursor = equip attachment
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {

            ItemStack cursorStack =
                    Minecraft.getInstance()
                            .player
                            .containerMenu
                            .getCarried();

            if (cursorStack.isEmpty())
                return;

            if (ModGunAttachments.ATTACHMENTS
                    .getForItemOrNull(cursorStack.getItem()) == null)
                return;

            event.setCanceled(true);

            PacketDistributor.sendToServer(
                    new EquipAttachmentPayload(slot.index)
            );
        }
    }
    /**
     * Cancels picking up the gun when the button is released
     * @param event
     */
    @SubscribeEvent
    public static void onMouseButtonReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (!removingAttachment)
            return;

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            event.setCanceled(true);
            removingAttachment = false;
        }
    }
    public static boolean playerShooting() {
        return wasPressed;
    }
    public static boolean playerHoldsGun(Player player) {
        return player.getMainHandItem().getItem() instanceof AbstractGunItem
                || player.getOffhandItem().getItem() instanceof AbstractGunItem;
    }
}