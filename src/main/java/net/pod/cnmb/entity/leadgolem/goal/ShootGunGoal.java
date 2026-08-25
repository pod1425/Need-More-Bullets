package net.pod.cnmb.entity.leadgolem.goal;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;
import net.pod.cnmb.entity.leadgolem.LeadGolem;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.registry.ModItems;

import java.util.EnumSet;

public class ShootGunGoal extends Goal {
    private final LeadGolem golem;

    private int cooldown;
    private final int cooldownTime;

    public ShootGunGoal(LeadGolem golem, int cooldownTime) {
        this.golem = golem;
        this.cooldownTime = cooldownTime;

        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (golem.getWeapon().isEmpty()) return false;
        if (golem.getTarget() == null) return false;

        return golem.getWeapon().is(ModItems.PISTOL.get());
    }

    @Override
    public void tick() {
        if (golem.getTarget() == null) {
            return;
        }

        golem.lookAt(EntityAnchorArgument.Anchor.EYES, golem.getTarget().getEyePosition());

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        ((AbstractGunItem) golem.getWeapon().getItem()).shoot(golem);

        cooldown = cooldownTime;
    }
}
