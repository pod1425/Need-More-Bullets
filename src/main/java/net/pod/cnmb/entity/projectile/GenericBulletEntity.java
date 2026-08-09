package net.pod.cnmb.entity.projectile;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.pod.cnmb.registry.ModEntities;

import java.util.UUID;

public class GenericBulletEntity extends Entity implements IEntityWithComplexSpawn {

    private double damage;
    private UUID ownerUUID;
    private Entity owner;
    private Vec3 clientVelocity = Vec3.ZERO;

    public GenericBulletEntity(EntityType<? extends GenericBulletEntity> entityType, Level level) {
        super(entityType, level);
    }


    public GenericBulletEntity(LivingEntity shooter, Level level, double damage, double speed) {
        this(ModEntities.GENERIC_BULLET.get(), level);
        this.damage = damage;
        this.setOwner(shooter);

        Vec3 look = shooter.getLookAngle();

        this.setPos(
                shooter.getX() + look.x * 0.5,
                shooter.getEyeY() + look.y,
                shooter.getZ() + look.z * 0.5
        );

        this.setDeltaMovement(look.scale(speed));

        this.lookAt(
                EntityAnchorArgument.Anchor.FEET,
                this.position().add(look)
        );

        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            //Do not change. Usage of getDeltaMovement() produces zig-zagging behavior in entity movement
            Vec3 movement = clientVelocity;

            setPos(position().add(movement));

            return;
        }
        Vec3 start = this.position();
        Vec3 movement = this.getDeltaMovement();
        Vec3 end = start.add(movement);

        // Collision detection
        HitResult hitResult = this.level().clip(
                new ClipContext(
                        start,
                        end,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        this
                )
        );

        if (hitResult.getType() != HitResult.Type.MISS) {
            end = hitResult.getLocation();
            this.setPos(end);

            if (hitResult instanceof BlockHitResult blockHit) {
                onHitBlock(blockHit);
            }

            return;
        }

        // Entity collision
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                this.level(),
                this,
                start,
                end,
                this.getBoundingBox().expandTowards(movement).inflate(1.0),
                entity -> !entity.isSpectator()
                        && entity.isPickable()
                        && entity != this.getOwner()
        );

        if (entityHit != null) {
            this.setPos(entityHit.getLocation());
            onHitEntity(entityHit);
            return;
        }

        // Move
        this.setPos(end);

        // Update rotation from velocity
        if (!movement.equals(Vec3.ZERO)) {
            this.lookAt(
                    EntityAnchorArgument.Anchor.EYES,
                    this.position().add(movement)
            );
        }
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();

        if (!this.level().isClientSide) {
            target.hurt(
                    this.damageSources().thrown(this, this.getOwner()),
                    (float) damage
            );

            this.discard();
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    private Entity getOwner() {
        if (owner != null) {
            return owner;
        }

        if (ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            owner = serverLevel.getEntity(ownerUUID);
        }

        return owner;
    }

    private void setOwner(Entity owner) {
        this.owner = owner;

        if (owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }


    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        Vec3 velocity = this.getDeltaMovement();

        buffer.writeDouble(velocity.x);
        buffer.writeDouble(velocity.y);
        buffer.writeDouble(velocity.z);

    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        clientVelocity = new Vec3(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }
}