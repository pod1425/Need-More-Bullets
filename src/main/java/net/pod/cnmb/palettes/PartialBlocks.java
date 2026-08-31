package net.pod.cnmb.palettes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.pod.cnmb.datagen.ModBlockTagProvider;
import net.pod.cnmb.datagen.ModRecipeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Here we have all blocks that are NOT full blocks but which origins from other blocks
 */
public enum PartialBlocks {
    STAIRS("stairs", StairBlock::new, PalettesBlockStateModelData::stairsBlockStateModel) {
        @Override
        public void generateTag(ModBlockTagProvider btp, Block b) {
            btp.pTag(BlockTags.STAIRS).add(b);
        }

        @Override
        public void generateRecipe(ModRecipeProvider mrp, RecipeOutput out, Block resultBlock, Block materialBlock, String advancementName) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultBlock, 4)
                    .pattern("B  ")
                    .pattern("BB ")
                    .pattern("BBB")
                    .define('B', materialBlock)
                    .unlockedBy(advancementName, mrp.pHas(materialBlock))
                    .save(out);
        }
    },

    WALL("wall", WallBlock::new, PalettesBlockStateModelData::wallBlockStateModel) {
        @Override
        public void generateTag(ModBlockTagProvider btp, Block b) {
            btp.pTag(BlockTags.WALLS).add(b);
        }

        @Override
        public void generateRecipe(ModRecipeProvider mrp, RecipeOutput out, Block resultBlock, Block materialBlock, String advancementName) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultBlock, 6)
                    .pattern("BBB")
                    .pattern("BBB")
                    .define('B', materialBlock)
                    .unlockedBy(advancementName, mrp.pHas(materialBlock))
                    .save(out);
        }
    },

    SLAB("slab", SlabBlock::new, PalettesBlockStateModelData::slabBlockStateModel) {
        @Override
        public void generateTag(ModBlockTagProvider btp, Block b) {
            btp.pTag(BlockTags.SLABS).add(b);
        }

        @Override
        public void generateRecipe(ModRecipeProvider mrp, RecipeOutput out, Block resultBlock, Block materialBlock, String advancementName) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultBlock, 6)
                    .pattern("BBB")
                    .define('B', materialBlock)
                    .unlockedBy(advancementName, mrp.pHas(materialBlock))
                    .save(out);
            // also register backcraft recipe, because slabs can be uncrafted to normal block
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, materialBlock)
                    .pattern("S")
                    .pattern("S")
                    .define('S', resultBlock)
                    .unlockedBy(advancementName, mrp.pHas(resultBlock))
                    .save(out);
        }
    };

    // For datagen
    public abstract void generateTag(ModBlockTagProvider btp, Block b);
    public abstract void generateRecipe(ModRecipeProvider mrp, RecipeOutput out, Block resultBlock, Block parentBlock, String advancementName);
    // For datagen

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
