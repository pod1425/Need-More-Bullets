package net.pod.cnmb.entity.beedrone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BeeDrone extends Mob {
    public final AnimationState flyAnimationState = new AnimationState();

    private Vec3 target;

    public BeeDrone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);

        this.moveControl = new FlyingMoveControl(this, 20, true);

        flyAnimationState.start(0);
    }

    public void setTarget(Vec3 newTarget) {
        target = newTarget;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (target != null) {
            if (distanceToSqr(target.x, target.y, target.z) < 0.25) {
                target = null;
                getNavigation().stop();
            } else {
                getNavigation().moveTo(target.x, target.y, target.z, 1.0);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (target != null) {
            tag.putDouble("TargetX", target.x);
            tag.putDouble("TargetY", target.y);
            tag.putDouble("TargetZ", target.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("TargetX") && tag.contains("TargetY") && tag.contains("TargetZ")) {
            target = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"));
        }
    }
}
