package net.pod.cnmb.item.gun.smg;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.smg.client.SMGRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

public class SMGItem extends AbstractGunItem {
    public SMGItem(Properties properties, int shootRate, double bulletDamage, double bulletSpeed, double inaccuracy, boolean isAutomatic) {
        super(properties, shootRate, bulletDamage, bulletSpeed, inaccuracy, isAutomatic, List.of());
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private SMGRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) renderer = new SMGRenderer();

                return renderer;
            }
        });
    }

    private static final RawAnimation
            FIRE = RawAnimation.begin().thenPlay("fire"),
            RELOAD = RawAnimation.begin().thenPlay("load").thenPlay("unload");

    @Override
    protected AnimationController<AbstractGunItem> getRawAnimations(AnimationController<AbstractGunItem> controller) {
        return controller
                .triggerableAnim("fire", FIRE)
                .triggerableAnim("reload", RELOAD);
    }

    @Override
    protected RawAnimation getReloadRawAnimation() {
        return RELOAD;
    }

    @Override
    public boolean shoot(Entity entity, ItemStack stack) {
        return shootWithAnim(entity, stack);
    }
}
