package net.pod.cnmb.event.gun;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GunAimEvent extends GunEvent {
    public GunAimEvent(Level level, Entity shooter, Entity targetEntity, ItemStack gunItem, BlockPos pos) {
        super(level, shooter, targetEntity, gunItem, pos);
    }
}
