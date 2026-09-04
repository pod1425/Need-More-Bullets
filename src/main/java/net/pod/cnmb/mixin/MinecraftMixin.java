package net.pod.cnmb.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.pod.cnmb.event.ModClientEvents;
import net.pod.cnmb.util.ControllableMob;
import net.pod.cnmb.util.CustomAttack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    public final List<String> allowedKeybinds = new ArrayList<>(List.of(
            "key.chat", "key.command"
    ));

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z"))
    private boolean consumeClick(KeyMapping key) {
        return ModClientEvents.controlling != null ? allowedKeybinds.contains(key.getName()) && key.consumeClick() : key.consumeClick();
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (ModClientEvents.controlling instanceof CustomAttack entity) {
            entity.attack();
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void startUseItem(CallbackInfo ci) {
        if (ModClientEvents.controlling != null) {
            ci.cancel();
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void continueAttack(boolean leftClick, CallbackInfo ci) {
        if (ModClientEvents.controlling != null) {
            ci.cancel();
        }
    }
}
