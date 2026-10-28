package net.pod.cnmb.item.gun.pistol;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.item.gun.pistol.client.PistolRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

public final class PistolItem extends AbstractGunItem {
    public PistolItem(Properties properties, int shootRate, double bulletDamage, double bulletSpeed, double inaccuracy, boolean isAutomatic) {
        super(properties, shootRate, bulletDamage, bulletSpeed, inaccuracy, isAutomatic, List.of(GunAttachmentSlot.SCOPE,
                GunAttachmentSlot.BARREL, GunAttachmentSlot.HANDLE, GunAttachmentSlot.MAGAZINE, GunAttachmentSlot.UNDERSIDE));
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
