package net.pod.cnmb.block.thermometer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.pod.cnmb.registry.ModBlockEntities;

public class ThermometerBlockEntity extends BlockEntity {
    private Integer biomeTemperature;
    private Integer timeTemperature;

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.THERMOMETER.get(), ThermometerRenderer::new);
    }

    public ThermometerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.THERMOMETER.get(), pos, blockState);
    }

    public void tick() {
        if (level != null && level.getGameTime() % 20 == 0) {
            int temperature = getTimeTemperature();
            if (temperature != timeTemperature) {
                timeTemperature = temperature;
                level.setBlock(getBlockPos(), getBlockState().setValue(ThermometerBlock.POWER, biomeTemperature + timeTemperature), 3);
            }
        }
    }

    public void updateTemperature() {
        biomeTemperature = (int) (4 * level.getBiome(getBlockPos()).value().getBaseTemperature()) + 3;
        timeTemperature = getTimeTemperature();
         level.setBlock(getBlockPos(), getBlockState().setValue(ThermometerBlock.POWER, biomeTemperature + timeTemperature), 3);
    }

    private int getTimeTemperature() {
        long time = level.getDayTime() % 24000L;
        return Mth.clamp(Mth.floor(2.0 + 2.0 * Math.cos(2.0 * Math.PI * (time - 6000) / 24000.0) + 0.5), 0, 4);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null && !level.isClientSide) updateTemperature();
    }
}