package net.pod.cnmb.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"))
    private double cameraX(LocalPlayer player) {
        return cameraPosition(player).x;
    }

    @Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"))
    private double cameraY(LocalPlayer player) {
        return cameraPosition(player).y;
    }

    @Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"))
    private double cameraZ(LocalPlayer player) {
        return cameraPosition(player).z;
    }

    private static Vec3 cameraPosition(LocalPlayer player) {
        Entity camera = Minecraft.getInstance().cameraEntity;
        return (camera == null ? player : camera).position();
    }
}