package net.pod.cnmb.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.pod.cnmb.event.ModClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void turnControlledEntity(LocalPlayer player, double yaw, double pitch) {
        if (ModClientEvents.controlling != null) {
            ModClientEvents.pitch = pitch / 2;
            ModClientEvents.yaw = yaw / 2;
            return;
        }

        player.turn(yaw, pitch);
    }
}