package net.pod.cnmb.item.gun.machine_gun;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.machine_gun.client.MachineGunRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

public class MachineGunItem extends AbstractGunItem {
    public MachineGunItem(Properties properties, int shootRate, double bulletDamage, double bulletSpeed, double inaccuracy, boolean isAutomatic) {
        super(properties, shootRate, bulletDamage, bulletSpeed, inaccuracy, isAutomatic, List.of());
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private MachineGunRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) renderer = new MachineGunRenderer();

                return renderer;
            }
        });
    }

    private static final RawAnimation RELOAD = RawAnimation.begin().thenPlay("reload");

    @Override
    protected AnimationController<AbstractGunItem> getRawAnimations(AnimationController<AbstractGunItem> controller) {
        return controller.triggerableAnim("reload", RELOAD);
    }

    @Override
    protected RawAnimation getReloadRawAnimation() {
        return RELOAD;
    }
}
