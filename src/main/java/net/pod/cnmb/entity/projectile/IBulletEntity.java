package net.pod.cnmb.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public interface IBulletEntity {
    void setOwner(Entity owner);
    Entity getOwner();
    void onHitBlock(BlockHitResult result);
    void onHitEntity(EntityHitResult result);
}
