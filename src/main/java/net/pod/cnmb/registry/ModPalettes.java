package net.pod.cnmb.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.pallete.Palette;
import net.pod.cnmb.block.pallete.PaletteBlock;
import net.pod.cnmb.block.pallete.PaletteType;
import net.pod.cnmb.block.pallete.ToolRequirement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModPalettes {
    private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(NeedMoreBulletsMod.MODID);
    public static final List<Palette> PALETTES = new ArrayList<>();
    private static final List<DeferredBlock<Block>> BLOCKS = new ArrayList<>();

    public static final Palette CERUSSITE = add("cerussite",
            List.of(PaletteType.VANILLA_STONE_TYPES),
            BlockBehaviour.Properties.of().strength(1.5f, 6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE))
            .setToolRequirement(ToolRequirement.PICKAXE);
    public static final Palette HORNY_SCULK = add("horny_sculk",
            List.of(PaletteType.EXTENDED_STONE_TYPES),
            BlockBehaviour.Properties.of().strength(1.5f, 6f).requiresCorrectToolForDrops().sound(SoundType.STONE),
            RotatedPillarBlock::new)
            .setToolRequirement(ToolRequirement.PICKAXE);

    public static Palette add(String name, List<PaletteType> types, BlockBehaviour.Properties properties) {
        return add(name, types, properties, Block::new);
    }
    public static Palette add(String name, List<PaletteType> types, BlockBehaviour.Properties properties,
                              Function<BlockBehaviour.Properties, Block> supplier) {
        Palette palette = new Palette(REGISTER, name, types, properties, supplier);
        PALETTES.add(palette);
        for (PaletteBlock block : palette.get()) {
            ModItems.add(block.name, block.deferred);
            BLOCKS.add(block.deferred);
            if (block.type != null && block.type.hasPartials()) {
                block.partials.forEach((p, b) -> {
                    ModItems.add(p.get(block.name), b);
                    BLOCKS.add(b);
                });
            }
        }
        return palette;
    }

    public static List<DeferredBlock<? extends Block>> getBlocks() {
        return List.copyOf(BLOCKS);
    }

    public static void register(IEventBus event) {
        REGISTER.register(event);
    }
}