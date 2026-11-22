package net.pod.cnmb.entity.beedrone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pod.cnmb.item.BeeDroneController;
import net.pod.cnmb.registry.ModDataComponents;
import net.pod.cnmb.registry.ModEntities;
import net.pod.cnmb.util.ControllableMob;
import net.pod.cnmb.util.CustomAttack;
import net.pod.cnmb.util.VecUtils;

import java.util.List;

public class BeeDrone extends ControllableMob implements CustomAttack {
    private static final float CAMERA_LERP = 0.2F;
    private static final double ACCELERATION = 0.03D;
    private static final double BRAKING = 0.08D;

    public static final int MAX_ENERGY = 1000;
    public static final int BATTERY_LEVELS = 4;

    public static final EntityDataAccessor<Float> DATA_BATTERY_LEVEL = SynchedEntityData.defineId(BeeDrone.class, EntityDataSerializers.FLOAT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(DATA_BATTERY_LEVEL, (float) BATTERY_LEVELS);
    }

    private final EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 128, 128, MAX_ENERGY);
    private final IEnergyStorage energyCapability = new IEnergyStorage() {
        @Override public int receiveEnergy(int toReceive, boolean simulate) {return energyStorage.receiveEnergy(toReceive, simulate);}
        @Override public int extractEnergy(int toExtract, boolean simulate) {return 0;}
        @Override public int getEnergyStored() {return energyStorage.getEnergyStored();}
        @Override public int getMaxEnergyStored() {return energyStorage.getMaxEnergyStored();}
        @Override public boolean canExtract() {return false;}
        @Override public boolean canReceive() {return true;}
    };

    public float getBatteryLevel() {
        return getEntityData().get(DATA_BATTERY_LEVEL);
    }

    public int getDisplayBatteryLevel() {
        float level = getBatteryLevel() + 1;
        int intLevel = Mth.floor(level);
        float fraction = level - intLevel;

        if (level <= 0.0F) return 0;
        if (intLevel == 0) return (level().getGameTime() / 10L) % 2 == 0 ? 1 : 0;
        if (fraction <= 0.4F) return (level().getGameTime() / 10L) % 2 == 0 ? intLevel - 1 : intLevel;
        return intLevel;
    }

    public final AnimationState flyAnimationState = new AnimationState();

    private int ambientSoundTimer;
    private int batterySyncTimer;
    private Vec3 target;

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, ModEntities.BEE_DRONE.get(), (be, d) -> be.energyCapability);
    }

    public BeeDrone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        flyAnimationState.start(0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 0.8)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    public boolean start(ServerPlayer player) {
        return energyStorage.getEnergyStored() > 0 && super.start(player);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof BeeDroneController) {
            if (player instanceof ServerPlayer) stack.set(ModDataComponents.BEE_DRONE_CONTROLLER_ID, getUUID());
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            int extracted = energyStorage.extractEnergy(1, false);
            if (isControlled() && extracted == 0) stop();

            if (++batterySyncTimer >= 5) {
                batterySyncTimer = 0;
                getEntityData().set(DATA_BATTERY_LEVEL, energyStorage.getEnergyStored() / (MAX_ENERGY / (float) BATTERY_LEVELS));
            }

            if (--ambientSoundTimer <= 0) {
                ambientSoundTimer = 90;

                level().playSound(null, this, SoundEvents.BEE_LOOP, SoundSource.NEUTRAL, 0.5F,
                        Mth.clamp(0.9F + (float) getDeltaMovement().horizontalDistance() * 0.25F, 0.9F, 3.4F));
            }

        }
    }

    public void setTarget(Vec3 newTarget) {
        target = newTarget;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void travel(Vec3 vec) {
        if (!isControlled()) {
            super.travel(vec);
            return;
        }

        if (input == null) return;

        float targetYaw = Mth.wrapDegrees(getYRot() + input.yaw());
        float targetPitch = Mth.clamp(getXRot() + input.pitch(), -90.0F, 90.0F);

        float yaw = Mth.rotLerp(CAMERA_LERP, getYRot(), targetYaw);
        float pitch = Mth.lerp(CAMERA_LERP, getXRot(), targetPitch);

        setRot(yaw, pitch);

        yBodyRotO = yBodyRot;
        yHeadRotO = yHeadRot;

        yBodyRot = yaw;
        yHeadRot = yaw;

        yaw *= Mth.DEG_TO_RAD;

        double x = 0;
        double y = 0;
        double z = 0;

        if (input.forward()) z += 1;
        if (input.back())    z -= 1;
        if (input.left())    x += 1;
        if (input.right())   x -= 1;
        if (input.up())      y += 1;
        if (input.down())    y -= 1;

        Vec3 direction = new Vec3(x, y, z);
        Vec3 desiredVelocity = direction.lengthSqr() == 0 ? Vec3.ZERO : direction.normalize().yRot(-yaw).scale(getControlSpeed());
        Vec3 velocity = getDeltaMovement().add(0, -0.008, 0);

        Vec3 delta;
        if (desiredVelocity.lengthSqr() > velocity.lengthSqr()) delta = desiredVelocity.subtract(velocity).scale(ACCELERATION);
        else delta = desiredVelocity.subtract(velocity).scale(BRAKING);

        velocity = velocity.add(delta);

        setDeltaMovement(velocity);
        move(MoverType.SELF, velocity);
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
            
            AABB area = new AABB(VecUtils.sub(pos, damageRadius), VecUtils.add(pos, damageRadius));
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
        tag.put("Energy", energyStorage.serializeNBT(level().registryAccess()));

        if (target != null) {
            tag.putDouble("TargetX", target.x);
            tag.putDouble("TargetY", target.y);
            tag.putDouble("TargetZ", target.z);
        }

        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Energy")) energyStorage.deserializeNBT(level().registryAccess(), tag.get("Energy"));

        if (tag.contains("TargetX") && tag.contains("TargetY") && tag.contains("TargetZ")) {
            target = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"));
        }

        super.readAdditionalSaveData(tag);
    }
}