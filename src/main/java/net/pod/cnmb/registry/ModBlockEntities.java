package net.pod.cnmb.registry;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.pallet.PalletBlockEntity;
import net.pod.cnmb.block.copper_flower.CopperFlowerBlockEntity;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NeedMoreBulletsMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PalletBlockEntity>> PALLET =
            registerType("pallet", PalletBlockEntity::new, ModBlocks.PALLET);

    public static final Supplier<BlockEntityType<CopperFlowerBlockEntity>> COPPER_FLOWER =
            BLOCK_ENTITY_TYPES.register("copper_flower_be", () ->
                    BlockEntityType.Builder.of(CopperFlowerBlockEntity::new, ModBlocks.COPPER_FLOWER.get())
                            .build(null));
    @SafeVarargs
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerType(String key, BlockEntityType.BlockEntitySupplier<T> factory, DeferredBlock<Block>... validBlocks) {
        var type = Util.fetchChoiceType(References.BLOCK_ENTITY, key);
        assert type != null;
        return BLOCK_ENTITY_TYPES.register(
                key,
                () -> BlockEntityType.Builder.of(
                        factory,
                        Arrays.stream(validBlocks).map(DeferredHolder::get).toArray(Block[]::new)
                ).build(type));
    }

    public static void register(IEventBus eventBus) { BLOCK_ENTITY_TYPES.register(eventBus); }
}