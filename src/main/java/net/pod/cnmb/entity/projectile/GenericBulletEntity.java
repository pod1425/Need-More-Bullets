package net.pod.cnmb.entity.projectile;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.pod.cnmb.registry.ModEntities;

import javax.annotation.Nullable;
import java.util.UUID;
/**
 This is serverside code that runs for every bullet.
 For clientside, check out GenericBulletRenderer
 */
public class GenericBulletEntity extends Entity implements IEntityWithComplexSpawn, IBulletEntity {
    private double damage;
    // only set if its a player
    private UUID ownerUUID;
    // to check who does the damage
    private Entity owner;
    // for fixing the issues with weird rendering on high speeds
    private Vec3 clientVelocity = Vec3.ZERO;

    /**
     * Shouldnt be used to create a bullet entity.
     * Use {@code GenericBulletEntity(Entity, Level, double, double, Vec3)} instead.
     * @param entityType
     * @param level
     */
    public GenericBulletEntity(EntityType<? extends GenericBulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Initializes a new bullet and fully prepares it to be added into the world.
     * Gets its vector of flight from shooter looking angle and applies inaccuracy.
     * Sets the vector of flight relative to its "feet", with its looking angle always the same
     * as the flight direction.
     * After object creation, immidiately ready to be added to the world with {@code Level#addFreshEntity}
     *
     * @param shooter the entity that shot this bullet, will be used to record who did the damage
     * @param level level duh
     * @param damage if I need to explain what this means, its already too late for you. Just go be a barista or something
     * @param speed speed at which bullet travels, in blocks per second
     * @param inaccuracy angle of spread of bullets when shooting
     * @param offset sets the offset for the bullet when it spawns. Origin point is at {@code shooter} X and Z coordinates,
     *               and their EyeY
     *               If this param is null, the default pos will be at the center of {@code shooter}'s head
     */
    public GenericBulletEntity(Entity shooter, Level level, double damage, double speed, double inaccuracy, @Nullable Vec3 offset) {
        this(ModEntities.GENERIC_BULLET.get(), level);
        this.damage = damage;
        this.setOwner(shooter);

        Vec3 look = shooter.getLookAngle();

        look = applyInaccuracy(
                look,
                inaccuracy,
                shooter.getRandom()
        );
        if (offset == null) {
            this.setPos(
                    shooter.getX() + look.x * 0.5,
                    shooter.getEyeY() + look.y,
                    shooter.getZ() + look.z * 0.5
            );
        } else {
            this.setPos(
                    shooter.getX() + offset.x,
                    shooter.getEyeY() + offset.y,
                    shooter.getZ() + offset.z
            );
        }

        this.setDeltaMovement(look.scale(speed));

        this.lookAt(
                EntityAnchorArgument.Anchor.FEET,
                this.position().add(look)
        );
    }
    private static Vec3 applyInaccuracy(Vec3 direction, double inaccuracy, RandomSource random) {
        if (inaccuracy <= 0.0) {
            return direction;
        }

        double angle = Math.toRadians(inaccuracy);

        double theta = random.nextDouble() * Math.PI * 2.0;
        double cos = Math.cos(angle * random.nextDouble());
        double sin = Math.sqrt(1.0 - cos * cos);

        Vec3 perpendicular = direction.cross(
                Math.abs(direction.y) < 0.999
                        ? new Vec3(0, 1, 0)
                        : new Vec3(1, 0, 0)
        ).normalize();

        Vec3 perpendicular2 = direction.cross(perpendicular).normalize();

        return direction.scale(cos)
                .add(perpendicular.scale(Math.cos(theta) * sin))
                .add(perpendicular2.scale(Math.sin(theta) * sin))
                .normalize();
    }
    /**
     * Initializes a new bullet and fully prepares it to be added into the world.
     * See {@code GenericBulletEntity(Entity, Level, double, double, Vec3)} for more details.
     */
    public GenericBulletEntity(Entity shooter, Level level, double damage, double speed, double inaccuracy) {
        this(shooter, level, damage, speed, inaccuracy, null);
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

        if (hitResult.getType() != HitResult.Type.MISS) {
            end = hitResult.getLocation();
            this.setPos(end);

            if (hitResult instanceof BlockHitResult blockHit) {
                onHitBlock(blockHit);
            }

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


    @Override
    public void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();

        if (!this.level().isClientSide) {
            target.hurt(
                    this.damageSources().thrown(this, this.getOwner()),
                    (float) damage
            );

            this.discard();
        }
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public Entity getOwner() {
        if (owner != null) {
            return owner;
        }

        if (ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            owner = serverLevel.getEntity(ownerUUID);
        }

        return owner;
    }

    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;

        if (owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    /**
     * Override this to make the projectile gravity-affected
     * @return gravity scale
     */
    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    /**
     * Needed alongside with {@code readSpawnData} to sync client and server bullet speed
     * for proper rendering.
     * @param buffer The packet data stream
     */
    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        Vec3 velocity = this.getDeltaMovement();

        buffer.writeDouble(velocity.x);
        buffer.writeDouble(velocity.y);
        buffer.writeDouble(velocity.z);

    }

    /**
     * See {@code writeSpawnData}.
     * @param buffer The packet data stream
     */
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        clientVelocity = new Vec3(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }
}