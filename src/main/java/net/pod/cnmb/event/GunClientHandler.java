package net.pod.cnmb.event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.networking.LeftClickPayload;
import net.pod.cnmb.networking.ModNetworking;

@EventBusSubscriber(
        modid = NeedMoreBulletsMod.MODID,
        value = Dist.CLIENT
)
public class GunClientHandler {
    // This should become a player capability, probably.
    // If it doesn't take too much processing time, that is.
    private static boolean wasPressed = false;

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
            return;
        }
        if (!playerHoldsGun(mc.player)) {
            wasPressed = false;
            return;
        }
        // if pressed, then pressed, if not pressed, then not pressed
        boolean pressed = mc.options.keyAttack.isDown();
        if (pressed != wasPressed) {
            wasPressed = pressed;
            PacketDistributor.sendToServer(
                    new ModNetworking.GunTriggerPayload(pressed)
            );
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