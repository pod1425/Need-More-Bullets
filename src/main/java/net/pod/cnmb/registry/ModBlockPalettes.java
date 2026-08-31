package net.pod.cnmb.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.pod.cnmb.palettes.BlockTypes;
import net.pod.cnmb.palettes.GeneratedPaletteBlock;
import net.pod.cnmb.palettes.Palette;

import java.util.ArrayList;
import java.util.List;

/**
 * In this class defined block palettes to not create each block by hands
 * Imagine you need to add one type of block (e.g. cerussite), than you need:
 *  - create base block
 *  - model for this block
 *  - block state for this block
 *  - connected textures if needed
 *  - define block properties (sound, strength, etc.)
 *  - define recipes
 *  - additional shit like stonecutting, blasting ....
 *  - and do it several times because you also need cut, polished, pillar and other types of same block
 * Or you can your block here, and do everything only 1 time, and then everything else will be generated automatically base on BlockTypes you added to block.
 * @see BlockTypes
 */
public class ModBlockPalettes {
    public static final List<Palette> palettes = new ArrayList<>();

    public static final Palette CERUSSITE = createPalette("cerussite", // destroy time and explosion resistance same as andesite 1.5f and 6f
            BlockBehaviour.Properties.of().strength(1.5f, 6f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE))
            .add(BlockTypes.PREPARED_VANILLA_STONE_RANGE)
            .minableWithPickaxe()
            .stoneCuttable()
    ;


    /**
     * @snippet
     * createPalette("my_cool_palette",
     *      BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE))
     *      .add(PREPARED_VANILLA_STONE_RANGE) // You can use prepared ranges here, to not define each type for e.g. stone range
     *      .add(WOODEN_DOOR) // Or you can define each block individually
     */
    private static Palette createPalette(String baseBlockName, BlockBehaviour.Properties baseBlockProperties) {
        Palette palette = new Palette(baseBlockName, baseBlockProperties);
        palettes.add(palette);
        return palette;
    }

    public static List<GeneratedPaletteBlock> getGeneratedBlocks() {
        List<GeneratedPaletteBlock> l = new ArrayList<>();
        palettes.forEach(p -> l.addAll(p.blockList));
        return l;
    }

    public static void register(IEventBus eventBus) {
        for (Palette p : palettes) {
            p.register(eventBus);
        }
    }
}
