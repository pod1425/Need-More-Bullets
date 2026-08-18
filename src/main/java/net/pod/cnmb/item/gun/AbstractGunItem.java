package net.pod.cnmb.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pod.cnmb.entity.projectile.GenericBulletEntity;
import net.pod.cnmb.registry.ModSounds;

public abstract class AbstractGunItem extends Item {
    private int shootRate;
    private double bulletDamage;
    private double bulletSpeed;

    public AbstractGunItem(Properties properties, int shootRate, double bulletDamage, double bulletSpeed) {
        super(properties);
        this.shootRate = shootRate;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
    }

    public int getShootRate() {
        return shootRate;
    }

    public void setShootRate(int shootRate) {
        this.shootRate = shootRate;
    }

    public void shoot(Entity entity) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return;
            }
            player.getCooldowns().addCooldown(this, 20 / shootRate);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        Level l = entity.level();
        if (!l.isClientSide) {
            // creating the object automatically fills in all the necessary data.
            // After initialization, entity is ready to be added to the level
            GenericBulletEntity projectile =
                    new GenericBulletEntity(entity, l, bulletDamage, bulletSpeed);

            l.addFreshEntity(projectile);

            l.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    ModSounds.Gun_Fire.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }

    public void startFiring(ServerPlayer player) {
        player.startUsingItem(InteractionHand.MAIN_HAND);
    }

    public void stopFiring(ServerPlayer player) {
        player.stopUsingItem();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int remainingUseDuration
    ) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            shoot(player);
        }
    }
}