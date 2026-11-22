package net.pod.cnmb.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.pod.cnmb.event.ModClientEvents;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Redirect(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;jumping:Z", opcode = Opcodes.GETFIELD))
    private boolean jumping(Input input) {
        if (ModClientEvents.controlling != null) {
            return false;
        }

        return input.jumping;
    }

    @Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
    private void isShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
        if (ModClientEvents.controlling != null) {
            cir.setReturnValue(false);
        }
    }
}