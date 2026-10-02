package net.pod.cnmb.entity.lead_golem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.pod.cnmb.entity.lead_golem.goal.ShootGunGoal;
import net.pod.cnmb.entity.lead_golem.client.LeadGolemRenderer;
import net.pod.cnmb.registry.ModEntities;
import net.pod.cnmb.registry.ModTags;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class LeadGolem extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LEAD_GOLEM.get(), LeadGolemRenderer::new);
    }

    public LeadGolem(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Monster.class, true));

        this.goalSelector.addGoal(1, new ShootGunGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getMainHandItem();
        ItemStack weapon = getWeaponItem();

        if (item.isEmpty()) {
            if (weapon.isEmpty()) return InteractionResult.FAIL;

            player.setItemInHand(InteractionHand.MAIN_HAND, weapon);
            setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        } else {
            if (!weapon.isEmpty() || !(item.is(ModTags.LEAD_GOLEM_EQUIPABLE))) return InteractionResult.FAIL;

            setItemInHand(InteractionHand.MAIN_HAND, item);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "movement", 10,
                        state -> state.isMoving() ? state.setAndContinue(getWeaponItem().isEmpty() ? WALK : WALK_ARMED) : PlayState.STOP)
        );
    }

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation WALK_ARMED = RawAnimation.begin().thenLoop("walk_armed");

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        spawnAtLocation(getWeaponItem());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        ItemStack weapon = getWeaponItem();
        if (!weapon.isEmpty()) tag.put("Weapon", weapon.save(registryAccess()));
        System.out.println("SAVING WEAPON: " + weapon);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Weapon")) setItemInHand(InteractionHand.MAIN_HAND, ItemStack.parse(registryAccess(), tag.get("Weapon")).orElse(ItemStack.EMPTY));
        System.out.println("LOADING WEAPON: " + tag.get("Weapon"));
    }
}