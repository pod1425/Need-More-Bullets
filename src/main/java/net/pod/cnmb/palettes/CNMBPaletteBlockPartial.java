package net.pod.cnmb.palettes;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.createmod.catnip.lang.Lang;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.registry.CNMBAllPaletteStoneTypes;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

/**
 * Copy of PaletteBlockPartial from create
 * @see com.simibubi.create.content.decoration.palettes.PaletteBlockPartial
 */
public abstract class CNMBPaletteBlockPartial<B extends Block> {

    public static final CNMBPaletteBlockPartial<StairBlock> STAIR = new CNMBPaletteBlockPartial.Stairs();
    public static final CNMBPaletteBlockPartial<SlabBlock> SLAB = new CNMBPaletteBlockPartial.Slab(false);
    public static final CNMBPaletteBlockPartial<SlabBlock> UNIQUE_SLAB = new CNMBPaletteBlockPartial.Slab(true);
    public static final CNMBPaletteBlockPartial<WallBlock> WALL = new CNMBPaletteBlockPartial.Wall();

    public static final CNMBPaletteBlockPartial<?>[] ALL_PARTIALS = {STAIR, SLAB, WALL};
    public static final CNMBPaletteBlockPartial<?>[] FOR_POLISHED = {STAIR, UNIQUE_SLAB, WALL};

    private String name;

    private CNMBPaletteBlockPartial(String name) {
        this.name = name;
    }

    public @NonnullType BlockBuilder<B, CreateRegistrate> create(String variantName, CNMBPaletteBlockPattern pattern,
                                                                 BlockEntry<? extends Block> block, CNMBAllPaletteStoneTypes variant) {
        String patternName = Lang.nonPluralId(pattern.createName(variantName));
        String blockName = patternName + "_" + this.name;

        BlockBuilder<B, CreateRegistrate> blockBuilder = NeedMoreBulletsMod.REGISTRATE
                .block(blockName, p -> createBlock(block))
                .blockstate((c, p) -> generateBlockState(c, p, variantName, pattern, block))
                .recipe((c, p) -> createRecipes(variant, block, c, p))
                .transform(b -> transformBlock(b, variantName, pattern));

        ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> itemBuilder = blockBuilder.item()
                .transform(b -> transformItem(b, variantName, pattern));

        if (canRecycle())
            itemBuilder.tag(variant.materialTag);

        return itemBuilder.build();
    }

    protected ResourceLocation getTexture(String variantName, CNMBPaletteBlockPattern pattern, int index) {
        return CNMBPaletteBlockPattern.toLocation(variantName, pattern.getTexture(index));
    }

    protected BlockBuilder<B, CreateRegistrate> transformBlock(BlockBuilder<B, CreateRegistrate> builder,
                                                               String variantName, CNMBPaletteBlockPattern pattern) {
        getBlockTags().forEach(builder::tag);
        return builder.transform(pickaxeOnly());
    }

    protected ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> transformItem(
            ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> builder, String variantName,
            CNMBPaletteBlockPattern pattern) {
        getItemTags().forEach(builder::tag);
        return builder;
    }

    protected boolean canRecycle() {
        return true;
    }

    protected abstract Iterable<TagKey<Block>> getBlockTags();

    protected abstract Iterable<TagKey<Item>> getItemTags();

    protected abstract B createBlock(Supplier<? extends Block> block);

    protected abstract void createRecipes(CNMBAllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock,
                                          DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p);

    protected abstract void generateBlockState(DataGenContext<Block, B> ctx, RegistrateBlockstateProvider prov,
                                               String variantName, CNMBPaletteBlockPattern pattern, Supplier<? extends Block> block);

    private static class Stairs extends CNMBPaletteBlockPartial<StairBlock> {

        public Stairs() {
            super("stairs");
        }

        @Override
        protected StairBlock createBlock(Supplier<? extends Block> block) {
            return new StairBlock(block.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(block.get()));
        }

        @Override
        protected void generateBlockState(DataGenContext<Block, StairBlock> ctx, RegistrateBlockstateProvider prov,
                                          String variantName, CNMBPaletteBlockPattern pattern, Supplier<? extends Block> block) {
            prov.stairsBlock(ctx.get(), getTexture(variantName, pattern, 0));
        }

        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return Arrays.asList(BlockTags.STAIRS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return Arrays.asList(ItemTags.STAIRS);
        }

        @Override
        protected void createRecipes(CNMBAllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock,
                                     DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
            RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
            p.stairs(DataIngredient.items(patternBlock.get()), category, c::get, c.getName(), false);
            p.stonecutting(DataIngredient.tag(type.materialTag), category, c::get, 1);
        }

    }

    private static class Slab extends CNMBPaletteBlockPartial<SlabBlock> {

        private boolean customSide;

        public Slab(boolean customSide) {
            super("slab");
            this.customSide = customSide;
        }

        @Override
        protected SlabBlock createBlock(Supplier<? extends Block> block) {
            return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(block.get()));
        }

        @Override
        protected boolean canRecycle() {
            return false;
        }

        @Override
        protected void generateBlockState(DataGenContext<Block, SlabBlock> ctx, RegistrateBlockstateProvider prov,
                                          String variantName, CNMBPaletteBlockPattern pattern, Supplier<? extends Block> block) {
            String name = ctx.getName();
            ResourceLocation mainTexture = getTexture(variantName, pattern, 0);
            ResourceLocation sideTexture = customSide ? getTexture(variantName, pattern, 1) : mainTexture;

            ModelFile bottom = prov.models()
                    .slab(name, sideTexture, mainTexture, mainTexture);
            ModelFile top = prov.models()
                    .slabTop(name + "_top", sideTexture, mainTexture, mainTexture);
            ModelFile doubleSlab;

            if (customSide) {
                doubleSlab = prov.models()
                        .cubeColumn(name + "_double", sideTexture, mainTexture);
            } else {
                doubleSlab = prov.models()
                        .getExistingFile(prov.modLoc(pattern.createName(variantName)));
            }

            prov.slabBlock(ctx.get(), bottom, top, doubleSlab);
        }

        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return Arrays.asList(BlockTags.SLABS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return Arrays.asList(ItemTags.SLABS);
        }

        @Override
        protected void createRecipes(CNMBAllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock,
                                     DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
            RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
            p.slab(DataIngredient.items(patternBlock.get()), category, c::get, c.getName(), false);
            p.stonecutting(DataIngredient.tag(type.materialTag), category, c::get, 2);
            DataIngredient ingredient = DataIngredient.items(c.get());
            ShapelessRecipeBuilder.shapeless(category, patternBlock.get())
                    .requires(ingredient.toVanilla())
                    .requires(ingredient.toVanilla())
                    .unlockedBy("has_" + c.getName(), ingredient.getCriterion(p))
                    .save(p, Create.ID + ":" + c.getName() + "_recycling");
        }

        @Override
        protected BlockBuilder<SlabBlock, CreateRegistrate> transformBlock(
                BlockBuilder<SlabBlock, CreateRegistrate> builder, String variantName, CNMBPaletteBlockPattern pattern) {
            builder.loot((lt, block) -> lt.add(block, lt.createSlabItemTable(block)));
            return super.transformBlock(builder, variantName, pattern);
        }

    }

    private static class Wall extends CNMBPaletteBlockPartial<WallBlock> {

        public Wall() {
            super("wall");
        }

        @Override
        protected WallBlock createBlock(Supplier<? extends Block> block) {
            return new WallBlock(BlockBehaviour.Properties.ofFullCopy(block.get()).forceSolidOn());
        }

        @Override
        protected ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> transformItem(
                ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> builder, String variantName,
                CNMBPaletteBlockPattern pattern) {
            builder.model((c, p) -> p.wallInventory(c.getName(), getTexture(variantName, pattern, 0)));
            return super.transformItem(builder, variantName, pattern);
        }

        @Override
        protected void generateBlockState(DataGenContext<Block, WallBlock> ctx, RegistrateBlockstateProvider prov,
                                          String variantName, CNMBPaletteBlockPattern pattern, Supplier<? extends Block> block) {
            prov.wallBlock(ctx.get(), pattern.createName(variantName), getTexture(variantName, pattern, 0));
        }

        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return Arrays.asList(BlockTags.WALLS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return Arrays.asList(ItemTags.WALLS);
        }

        @Override
        protected void createRecipes(CNMBAllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock,
                                     DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
            RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
            p.stonecutting(DataIngredient.tag(type.materialTag), category, c::get, 1);
            DataIngredient ingredient = DataIngredient.items(patternBlock.get());
            ShapedRecipeBuilder.shaped(category, c.get(), 6)
                    .pattern("XXX")
                    .pattern("XXX")
                    .define('X', ingredient.toVanilla())
                    .unlockedBy("has_" + p.safeName(ingredient), ingredient.getCriterion(p))
                    .save(p, p.safeId(c.get()));
        }

    }

}

