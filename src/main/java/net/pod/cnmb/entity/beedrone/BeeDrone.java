package net.pod.cnmb.entity.beedrone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pod.cnmb.util.ControllableMob;
import net.pod.cnmb.util.CustomAttack;

import java.util.List;

public class BeeDrone extends ControllableMob implements CustomAttack {
    public final AnimationState flyAnimationState = new AnimationState();

    private Vec3 target;

    public BeeDrone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);

        flyAnimationState.start(0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    public void setTarget(Vec3 newTarget) {
        target = newTarget;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isControlled() && input != null) {
            float yaw = getYRot() * Mth.DEG_TO_RAD;

            double x = 0;
            double y = 0;
            double z = 0;

            if (input.forward()) z += 1;
            if (input.back())    z -= 1;
            if (input.left())    x += 1;
            if (input.right())   x -= 1;
            if (input.up())      y += 1;
            if (input.down())    y -= 1;

            Vec3 movement = new Vec3(x, y, z).yRot(-yaw);

            if (movement.lengthSqr() > 0) {
                movement = movement.normalize().scale(getControlSpeed());
            }

            move(MoverType.SELF, movement);
            return;
        }

        super.travel(travelVector);
    }

    @Override
    protected double getControlSpeed() {
        return getAttributeValue(Attributes.FLYING_SPEED);
    }

    @Override
    public void attack() {
        if (level() instanceof ServerLevel level) {
            Vec3 pos = position();

            level.explode(
                    controller, pos.x, this.getY(0.0625D), pos.z,
                    2.0F, false, Level.ExplosionInteraction.TNT
            );

            double damageRadius = 2.5;
            double maxDamage = 20;
            
            AABB area = new AABB(
                    pos.x - damageRadius, pos.y - damageRadius, pos.z - damageRadius,
                    pos.x + damageRadius, pos.y + damageRadius, pos.z + damageRadius
            );
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);
            targets.remove(this);


            DamageSource source = damageSources().explosion(null, controller);
            targets.forEach(target -> {
                AABB box = target.getBoundingBox();

                double distance = Math.sqrt(new Vec3(
                        Mth.clamp(pos.x, box.minX, box.maxX),
                        Mth.clamp(pos.y, box.minY, box.maxY),
                        Mth.clamp(pos.z, box.minZ, box.maxZ))
                        .distanceToSqr(pos));

                if (distance > damageRadius) return;

                double factor  = 1.0 - distance / damageRadius;

                target.hurt(source, (float)(maxDamage * factor));

                Vec3 delta = target.position().subtract(pos).normalize().scale(2 * factor);
                target.setDeltaMovement(target.getDeltaMovement().add(delta.x, 0.7 * factor, delta.z));
                target.hurtMarked = true;
            });

            stop();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if (target != null) {
            tag.putDouble("TargetX", target.x);
            tag.putDouble("TargetY", target.y);
            tag.putDouble("TargetZ", target.z);
        }

        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("TargetX") && tag.contains("TargetY") && tag.contains("TargetZ")) {
            target = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"));
        }

        super.readAdditionalSaveData(tag);
    }
}
