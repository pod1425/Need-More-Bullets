package net.pod.cnmb.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NeedMoreBulletsMod.MODID);

    public static final Supplier<BlockEntityType<RedStoneDepleterBlockEntity>> REDSTONE_DEPLETER =
            BLOCK_ENTITY_TYPES.register("redstone_depleter_be", () ->
                    BlockEntityType.Builder.of(RedStoneDepleterBlockEntity::new, ModBlocks.REDSTONE_DEPLETER.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
