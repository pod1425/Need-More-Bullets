package net.pod.cnmb.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.pod.cnmb.palettes.BlockTypes;
import net.pod.cnmb.palettes.Palette;

import java.util.ArrayList;
import java.util.List;

public class ModBlockPalettes {
    public static final List<Palette> palettes = new ArrayList<>();

    public static final Palette CERUSSITE = createPalette("cerussite",
            BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE))
            .add(BlockTypes.PREPARED_VANILLA_STONE_RANGE)
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

    public static void register(IEventBus eventBus) {
        for (Palette p : palettes) {
            p.register(eventBus);
        }
    }
}
