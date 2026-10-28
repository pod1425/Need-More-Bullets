package net.pod.cnmb.entity.lead_golem.goal;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.registry.ModTags;

public class ShootGunGoal extends Goal {
    private final LeadGolem golem;
    private int cooldown;

    public ShootGunGoal(LeadGolem golem) {
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        if (golem.getTarget() == null) return false;
        ItemStack weapon = golem.getWeaponItem();
        return !weapon.isEmpty() && weapon.is(ModTags.LEAD_GOLEM_EQUIPABLE);
    }

    @Override
    public void tick() {
        golem.lookAt(EntityAnchorArgument.Anchor.EYES, golem.getTarget().getEyePosition());

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        AbstractGunItem gun = (AbstractGunItem) golem.getWeaponItem().getItem();
        gun.shoot(golem);
        cooldown = 20 / gun.getBaseShootRate();
    }
}