package net.pod.cnmb.entity.lead_golem;

import com.simibubi.create.AllItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.IShearable;
import net.pod.cnmb.NeedMoreBulletsMod;
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

public class LeadGolem extends PathfinderMob implements GeoEntity, Shearable, IShearable {
    public enum Clothes {
        MAID("textures/entity/lead_golem/lead_golem_maid.png"),
        HELMET("textures/entity/lead_golem/lead_golem_helmet.png");

        public final ResourceLocation resource;

        Clothes(String path) {
            resource = NeedMoreBulletsMod.asResource(path);
        }
    }

    private static final EntityDataAccessor<Integer> CLOTHES = SynchedEntityData.defineId(LeadGolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(LeadGolem.class, EntityDataSerializers.INT);

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLOTHES, -1);
        builder.define(COLOR, 0xFFFFFFFF);
    }

    public Clothes getClothes() {
        int i = entityData.get(CLOTHES);
        Clothes[] all = Clothes.values();
        return i >= 0 && i < all.length ? all[i] : null;
    }

    public void setClothes(Clothes c) {
        entityData.set(CLOTHES, c == null ? -1 : c.ordinal());
    }

    public int getClothesColor() {
        return entityData.get(COLOR);
    }

    public void setClothesColor(int argb) {
        entityData.set(COLOR, argb | 0xFF000000);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean server = !level().isClientSide;
        ItemStack item = player.getItemInHand(hand);
        Clothes clothes = getClothes();

        if ((item.is(Items.STRING) || item.is(AllItems.CARDBOARD)) && clothes == null) {
            if (server) {
                setClothes(item.is(Items.STRING) ? Clothes.MAID : Clothes.HELMET);
                setClothesColor(0xFFFFFFFF);
                item.shrink(1);
                player.setItemInHand(hand, item.isEmpty() ? ItemStack.EMPTY : item);
            }
        }
        else if (item.getItem() instanceof DyeItem dye && clothes != null) {
            int color = dye.getDyeColor().getTextureDiffuseColor();
            if (color == getClothesColor()) return InteractionResult.FAIL;
            if (server) {
                setClothesColor(color);
                item.shrink(1);
                player.setItemInHand(InteractionHand.MAIN_HAND, item.isEmpty() ? ItemStack.EMPTY : item);
            }
        }
        else if (item.is(Items.POTION) && clothes != null && item.get(DataComponents.POTION_CONTENTS).is(Potions.WATER) && getClothesColor() != 0xFFFFFFFF) {
            if (server) {
                setClothesColor(0xFFFFFFFF);
                player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                playSound(SoundEvents.GENERIC_SPLASH);
            }
        }
        else {
            ItemStack weapon = getWeaponItem();

            if (item.isEmpty()) {
                if (weapon.isEmpty()) return InteractionResult.FAIL;
                if (server) {
                    player.setItemInHand(hand, weapon);
                    setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            } else {
                if (!weapon.isEmpty() || !(item.is(ModTags.LEAD_GOLEM_EQUIPABLE))) return InteractionResult.FAIL;
                if (server) {
                    setItemInHand(InteractionHand.MAIN_HAND, item);
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
            }
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
        Clothes clothes = getClothes();
        if (clothes != null) {
            tag.putString("Clothes", clothes.name());
            tag.putInt("Clothes color", getClothesColor());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        setItemInHand(InteractionHand.MAIN_HAND, tag.contains("Weapon") ? ItemStack.parse(registryAccess(), tag.get("Weapon")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY);
        setClothes(tag.contains("Clothes") ? Clothes.valueOf(tag.getString("Clothes")) : null);
        setClothesColor(tag.contains("Clothes color") ? tag.getInt("Clothes color") : 0xFFFFFFFF);
    }

    @Override
    public void shear(SoundSource soundSource) {
        spawnAtLocation(getClothes() == Clothes.MAID ? Items.STRING : AllItems.CARDBOARD);
        setClothes(null);
    }

    @Override
    public boolean readyForShearing() {
        return getClothes() != null;
    }
}