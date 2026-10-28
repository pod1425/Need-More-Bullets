package net.pod.cnmb.item.gun.musket;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.item.gun.musket.client.MusketRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

public class MusketItem extends AbstractGunItem {
    public MusketItem(Properties properties, int shootRate, double bulletDamage, double bulletSpeed, double inaccuracy, boolean isAutomatic) {
        super(properties, shootRate, bulletDamage, bulletSpeed, inaccuracy, isAutomatic, List.of(GunAttachmentSlot.SCOPE));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private MusketRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) renderer = new MusketRenderer();

                return renderer;
            }
        });
    }

    private static final RawAnimation FIRE = RawAnimation.begin().thenPlay("fire");

    @Override
    protected AnimationController<AbstractGunItem> getRawAnimations(AnimationController<AbstractGunItem> controller) {
        return controller.triggerableAnim("fire", FIRE);
    }

    @Override
    protected RawAnimation getReloadRawAnimation() {
        return null;
    }

    @Override
    public boolean shoot(Entity entity, ItemStack stack) {
        return shootWithAnim(entity, stack);
    }

    @Override
    public void reload(Entity entity, ItemStack item) {}
}
