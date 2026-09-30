package net.pod.cnmb.block.slip_roller;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.pod.cnmb.block.slip_roller.client.SlipRollerBlockRenderer;
import net.pod.cnmb.block.slip_roller.client.SlipRollerItemRenderer;
import net.pod.cnmb.registry.ModBlockEntities;
import net.pod.cnmb.registry.ModItems;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SlipRollerBlockEntity extends KineticBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SLIP_ROLLER.get(), SlipRollerBlockRenderer::new);
    }
    public static void registerRenderer(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new IClientItemExtensions() {
                    private final SlipRollerItemRenderer renderer = new SlipRollerItemRenderer();

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return renderer;
                    }
                },
                ModItems.SLIP_ROLLER.get()
        );
    }

    public SlipRollerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SLIP_ROLLER.get(), pos, blockState);
    }

    @Override
    public float calculateStressApplied() {
        return 4;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}