package net.pod.cnmb.palettes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Here we have all blocks that are NOT full blocks but which origins from other blocks
 */
public enum PartialBlocks {
    STAIRS("stairs", StairBlock::new, PalettesBlockStateModelData::stairsBlockStateModel),
    WALL("wall", WallBlock::new, PalettesBlockStateModelData::wallBlockStateModel),
    SLAB("slab", SlabBlock::new, PalettesBlockStateModelData::slabBlockStateModel);

    private final String extName;
    private @Nullable BiFunction<BlockState, BlockBehaviour.Properties, ? extends Block> statedBlockFactory = null;
    private @Nullable Function<BlockBehaviour.Properties, ? extends Block> blockFactory = null;
    private final PalettesBlockStateModelData.PalettesDataGenConsumer<? super Block> blockStateModelDatagenFactory;

    public String parseName(String baseName) {
        return baseName + "_" + extName;
    }

    /**
     * @snippet
     * Block block = () -> PartialBlocks.makeBlock(s, p)
     */
    @SuppressWarnings("unchecked")
    public <T extends Block> T makeBlock(BlockState s, BlockBehaviour.Properties p) {
        if (statedBlockFactory != null) {
            return (T) statedBlockFactory.apply(s, p);
        }

        assert blockFactory != null;
        return (T) blockFactory.apply(p);
    }

    public <T extends Block> void genBlockStateModel(BlockStateProvider bsp, T b, String bbn) {
        this.blockStateModelDatagenFactory.accept(bsp, b, bbn);
    }

    @SuppressWarnings("NullableProblems")
    PartialBlocks(String extName, BiFunction<BlockState, BlockBehaviour.Properties, ? extends Block> statedBlockFactory,
                  PalettesBlockStateModelData.PalettesDataGenConsumer<? super Block> blockStateModelDatagenFactory) {
        this.extName = extName;
        this.statedBlockFactory = statedBlockFactory;
        this.blockStateModelDatagenFactory = blockStateModelDatagenFactory;
    }

    @SuppressWarnings("NullableProblems")
    PartialBlocks(String extName, Function<BlockBehaviour.Properties, ? extends Block> blockFactory,
                  PalettesBlockStateModelData.PalettesDataGenConsumer<? super Block> blockStateModelDatagenFactory) {
        this.extName = extName;
        this.blockFactory = blockFactory;
        this.blockStateModelDatagenFactory = blockStateModelDatagenFactory;
    }
}
