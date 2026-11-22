package net.pod.cnmb.registry;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
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

import java.util.Arrays;

public class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NeedMoreBulletsMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PalletBlockEntity>> PALLET =
            registerType("pallet", PalletBlockEntity::new, ModBlocks.PALLET);

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
