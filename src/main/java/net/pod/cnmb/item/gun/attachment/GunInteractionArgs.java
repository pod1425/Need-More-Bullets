package net.pod.cnmb.item.gun.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pod.cnmb.item.gun.AbstractGunItem;

public class GunInteractionArgs {
    private final Level level;
    private final Entity shooter;
    private final Entity targetEntity;
    private final ItemStack gunItem;
    private final BlockPos pos;

    public GunInteractionArgs(Level level, Entity shooter, Entity targetEntity, ItemStack gunItem, BlockPos pos) {
        this.level = level;
        this.shooter = shooter;
        this.gunItem = gunItem;
        this.targetEntity = targetEntity;
        this.pos = pos;
    }

    public Level getLevel() {
        return level;
    }

    public Entity getShooter() {
        return shooter;
    }

    public ItemStack getGunItem() {
        return gunItem;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public BlockPos getPos() {
        return pos;
    }
}
