package net.pod.cnmb.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.thermometer.ThermometerBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NeedMoreBulletsMod.MODID);

    public static final Supplier<BlockEntityType<ThermometerBlockEntity>> THERMOMETER =
            BLOCK_ENTITY_TYPES.register("thermometer_be", () ->
                    BlockEntityType.Builder.of(ThermometerBlockEntity::new, ModBlocks.THERMOMETER.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
