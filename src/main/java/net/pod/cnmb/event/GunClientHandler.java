package net.pod.cnmb.event;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
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

    private static boolean wasPressed = false;
    @SubscribeEvent
    public static void onInteractionKey(
            InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        ItemStack stack = minecraft.player.getMainHandItem();

        if (!(stack.getItem() instanceof AbstractGunItem)) {
            return;
        }
        // Prevent vanilla attack processing
        event.setCanceled(true);
        // Prevent the hand swing
        event.setSwingHand(false);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        ItemStack stack = mc.player.getMainHandItem();

        if (!(stack.getItem() instanceof AbstractGunItem)) {
            wasPressed = false;
            return;
        }

        boolean pressed = mc.options.keyAttack.isDown();
        if (pressed != wasPressed) {
            wasPressed = pressed;

            PacketDistributor.sendToServer(
                    new ModNetworking.GunTriggerPayload(pressed)
            );
        }
    }
}