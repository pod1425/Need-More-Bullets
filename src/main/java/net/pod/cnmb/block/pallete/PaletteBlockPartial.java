package net.pod.cnmb.block.pallete;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.pod.cnmb.datagen.ModBlockTagProvider;
import net.pod.cnmb.datagen.ModRecipeProvider;

public enum PaletteBlockPartial {
    STAIRS("stairs", PaletteBlockStateModelData::stairsBlockStateModel) {
        @Override
        public void generateTag(ModBlockTagProvider btp, Block b) { btp.tag(BlockTags.STAIRS).add(b); }

        @Override
        public void generateRecipe(RecipeOutput out, Block resultBlock, Block materialBlock, String advancementName) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultBlock, 4)
                    .pattern("B  ")
                    .pattern("BB ")
                    .pattern("BBB")
                    .define('B', materialBlock)
                    .unlockedBy(advancementName, ModRecipeProvider.has(materialBlock))
                    .save(out);
        }
    },
    WALL("wall", PaletteBlockStateModelData::wallBlockStateModel) {
        @Override
        public void generateTag(ModBlockTagProvider btp, Block b) { btp.tag(BlockTags.WALLS).add(b); }

        @Override
        public void generateRecipe(RecipeOutput out, Block resultBlock, Block materialBlock, String advancementName) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultBlock, 6)
                    .pattern("BBB")
                    .pattern("BBB")
                    .define('B', materialBlock)
                    .unlockedBy(advancementName, ModRecipeProvider.has(materialBlock))
                    .save(out);
        }
    },
    SLAB("slab", PaletteBlockStateModelData::slabBlockStateModel) {
        @Override
        public void generateTag(ModBlockTagProvider btp, Block b) { btp.tag(BlockTags.SLABS).add(b); }

        @Override
        public void generateRecipe(RecipeOutput out, Block resultBlock, Block materialBlock, String advancementName) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, resultBlock, 6)
                    .pattern("BBB")
                    .define('B', materialBlock)
                    .unlockedBy(advancementName, ModRecipeProvider.has(materialBlock))
                    .save(out);
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, materialBlock)
                    .pattern("S")
                    .pattern("S")
                    .define('S', resultBlock)
                    .unlockedBy(advancementName, ModRecipeProvider.has(resultBlock))
                    .save(out);
        }
    };

    private final String name;
    private final DataGenConsumer<Block> dataGenFactory;

    PaletteBlockPartial(String name, DataGenConsumer<Block> dataGenFactory) {
        this.name = name;
        this.dataGenFactory = dataGenFactory;
    }

    public String get(String block) {
        return block + "_" + name;
    }

    public Block create(Block base, BlockBehaviour.Properties properties) {
        return switch (this) {
            case STAIRS -> new StairBlock(base.defaultBlockState(), properties);
            case WALL -> new WallBlock(properties);
            case SLAB -> new SlabBlock(properties);
        };
    }

    public void generateBlockStateModel(BlockStateProvider provider, Block block, String baseBlockName) {
        dataGenFactory.accept(provider, block, baseBlockName);
    }

    public abstract void generateTag(ModBlockTagProvider btp, Block b);
    public abstract void generateRecipe(RecipeOutput out, Block resultBlock, Block parentBlock, String advancementName);

    @FunctionalInterface
    public interface DataGenConsumer<T extends Block> {
        void accept(BlockStateProvider provider, T block, String baseBlockName);
    }
}