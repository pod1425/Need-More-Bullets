package net.pod.cnmb.item.gun;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pod.cnmb.entity.projectile.GenericBulletEntity;

public abstract class AbstractGunItem extends Item {
    // change into DataComponents later
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

    public void shoot(Player player) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }
        Level l = player.level();
        player.getCooldowns().addCooldown(this, 20 / shootRate);

        if (!l.isClientSide) {
            // creating the object automatically fills in all the necessary data.
            // After initialization, entity is ready to be added to the level
            GenericBulletEntity projectile =
                    new GenericBulletEntity(player, l, bulletDamage, bulletSpeed);

            l.addFreshEntity(projectile);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
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

    /*
            projectile.setYRot((float)(Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F);

            projectile.setXRot((float)(
                    Mth.atan2(direction.y,
                            Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * Mth.RAD_TO_DEG)
            );
            */
}
