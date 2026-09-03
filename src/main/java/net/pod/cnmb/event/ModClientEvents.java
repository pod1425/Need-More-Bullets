package net.pod.cnmb.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.networking.ControlInputPayload;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.networking.StopControlPayload;
import net.pod.cnmb.registry.ModKeyBinds;
import net.pod.cnmb.util.ControllableMob;

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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (controlling != null) {
            Options opt = Minecraft.getInstance().options;

            ModNetworking.sendControlInput(new ControlInputPayload((float) pitch, (float) yaw,
                    opt.keyUp.isDown(), opt.keyDown.isDown(),
                    opt.keyLeft.isDown(), opt.keyRight.isDown(),
                    opt.keyJump.isDown(), opt.keyShift.isDown()));

            if (ModKeyBinds.STOP_CONTROL.consumeClick()) ModNetworking.sendStopControl(new StopControlPayload(controlling.getId()));
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
}

