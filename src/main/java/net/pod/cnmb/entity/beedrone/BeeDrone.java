package net.pod.cnmb.entity.beedrone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pod.cnmb.util.ControllableMob;
import net.pod.cnmb.util.CustomAttack;

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
    public void attack() {}

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
