package net.pod.cnmb.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.pod.cnmb.util.ControllableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class TrackedEntityMixin {
    @Shadow
    private Entity entity;

    @ModifyVariable(method = "updatePlayer", at = @At("STORE"))
    private boolean forceControllerTracking(boolean flag, ServerPlayer player) {
        if (entity instanceof ControllableMob mob && mob.isControlled()) {
            return true;
        }

        ControllableMob controlled = ControllableMob.controls.get(player.getUUID());
        if (controlled != null) {
            double range = entity.getType().clientTrackingRange() * 16.0;
            if (entity.distanceToSqr(controlled) <= range * range) {
                return true;
            }
        }

        return flag;
    }
}
