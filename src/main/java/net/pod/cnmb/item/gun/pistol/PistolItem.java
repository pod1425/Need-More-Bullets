package net.pod.cnmb.item.gun.pistol;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.pistol.client.PistolRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public final class PistolItem extends AbstractGunItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PistolItem(Properties properties, int shootRate, double bulletDamage, double bulletSpeed, double inaccuracy, boolean isAutomatic) {
        super(properties, shootRate, bulletDamage, bulletSpeed, inaccuracy, isAutomatic);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PistolRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) renderer = new PistolRenderer();

                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", 0, state -> PlayState.STOP)
                        .triggerableAnim("fire", FIRE)
                        .triggerableAnim("load", LOAD)
                        .triggerableAnim("unload", UNLOAD)
        );
    }

    private static final RawAnimation
            FIRE = RawAnimation.begin().thenPlay("fire"),
            LOAD = RawAnimation.begin().thenPlay("load"),
            UNLOAD = RawAnimation.begin().thenPlay("unload");

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean shoot(Entity entity) {
        boolean shot = super.shoot(entity);
        if (shot && entity instanceof Player player && player.level() instanceof ServerLevel level) {
            triggerAnim(player, GeoItem.getOrAssignId(player.getMainHandItem(), level), "controller", "fire");
        }

        return shot;
    }
}
