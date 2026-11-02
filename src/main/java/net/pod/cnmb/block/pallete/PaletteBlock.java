package net.pod.cnmb.block.pallete;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class PaletteBlock {
    public final @Nullable PaletteType type;
    public final String baseName;
    public final String name;
    public final String texture;

    public final DeferredBlock<Block> deferred;
    public final Map<PaletteBlockPartial, DeferredBlock<Block>> partials = new EnumMap<>(PaletteBlockPartial.class);

    public PaletteBlock(DeferredRegister.Blocks register, BlockBehaviour.Properties properties, String baseName, @Nullable PaletteType type) {
        this(register, Block::new, properties, baseName, type);
    }
    public PaletteBlock(DeferredRegister.Blocks register, Function<BlockBehaviour.Properties, Block> supplier, BlockBehaviour.Properties properties,
                        String baseName, @Nullable PaletteType type) {
        this.type = type;
        this.baseName = baseName;
        this.name = type == null ? baseName : type.get(baseName);
        this.texture = "block/palettes/" + baseName + '/' + name;
        this.deferred = register.register(name, type == null ? () -> supplier.apply(properties) : () -> type.getBlock(properties));

        if (type != null && type.hasPartials()) {
            for (PaletteBlockPartial partial : PaletteBlockPartial.values()) {
                partials.put(partial, register.register(partial.get(name), () -> partial.create(deferred.get(), properties)));
            }
        }
    }
}