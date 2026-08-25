package net.pod.cnmb.entity.leadgolem;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pod.cnmb.entity.leadgolem.goal.ShootGunGoal;
import net.pod.cnmb.registry.ModTags;
import org.jetbrains.annotations.Nullable;

public class LeadGolem extends Animal {
    private static final EntityDataAccessor<ItemStack> WEAPON =
            SynchedEntityData.defineId(LeadGolem.class, EntityDataSerializers.ITEM_STACK);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WEAPON, ItemStack.EMPTY);
    }

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public ItemStack getWeapon() {
        return entityData.get(WEAPON);
    }

    public void setWeapon(ItemStack weapon) {
        entityData.set(WEAPON, weapon);
    }

    public LeadGolem(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    protected void registerGoals() {
        //Brain of golem
        //this.goalSelector.addGoal(0,new FloatGoal(this));

        this.targetSelector.addGoal(0,new NearestAttackableTargetGoal<>(this, Monster.class, true));

        this.goalSelector.addGoal(0, new ShootGunGoal(this, 20));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes(){
        return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0 ){
            this.idleAnimationTimeout = 20;
            this.idleAnimationState.start(this.tickCount);
        }else{
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()){
            this.setupAnimationStates();
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getMainHandItem();

        if (item.isEmpty()) {
            if (getWeapon().isEmpty()) return InteractionResult.PASS;

            player.setItemInHand(hand, getWeapon());
            setWeapon(ItemStack.EMPTY);
        } else {
            if (!item.is(ModTags.Items.LEAD_GOLEM_EQUIPABLE)) return InteractionResult.PASS;

            ItemStack newItem = getWeapon();
            setWeapon(item);
            player.setItemInHand(hand, newItem);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (!getWeapon().isEmpty()) {
            compound.put("Weapon", getWeapon().save(registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("Weapon")) {
            setWeapon(ItemStack.parse(registryAccess(), compound.getCompound("Weapon")).orElse(ItemStack.EMPTY));
        }
    }
}
