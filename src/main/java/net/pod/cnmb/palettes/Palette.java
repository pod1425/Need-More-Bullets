package net.pod.cnmb.palettes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.datagen.ModBlockLootTableProvider;
import net.pod.cnmb.datagen.ModBlockTagProvider;
import net.pod.cnmb.datagen.ModRecipeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Palette {
    private final List<BlockTypes> types = new ArrayList<>();
    public final List<GeneratedPaletteBlock> blockList = new ArrayList<>();

    private final String baseBlockName;
    private final BlockBehaviour.Properties baseBlockProperties;

    private Boolean stoneCuttable = false;
    private Boolean minableWithPickaxe = false;

    public Palette(String baseBlockName, BlockBehaviour.Properties baseBlockProperties) {
        this.baseBlockName = baseBlockName;
        this.baseBlockProperties = baseBlockProperties;
    }

    public Palette add(BlockTypes type) {
        types.add(type);
        return this;
    }

    public Palette add(BlockTypes[] types) {
        this.types.addAll(List.of(types));
        return this;
    }

    public Palette stoneCuttable() {
        this.stoneCuttable = true;
        return this;
    }
    public Palette minableWithPickaxe() {
        this.minableWithPickaxe = true;
        return this;
    }

    public @Nullable DeferredBlock<? extends  Block> getByType(BlockTypes type) {
        var b = blockList.stream().filter(gb -> Objects.equals(gb.getBlockType(), type)).findFirst();
        return b.<DeferredBlock<? extends Block>>map(generatedPaletteBlock -> generatedPaletteBlock.deferredBlock).orElse(null);
    }

    public void register(IEventBus eventBus) {
        // Don't forget to register base block
        blockList.add(new GeneratedPaletteBlock(baseBlockName, baseBlockProperties));

        for (BlockTypes type : types) {
            var typedBlock = new GeneratedPaletteBlock(baseBlockName, baseBlockProperties, type);
            blockList.add(typedBlock);
            if (type.hasPartials()) {
                for(PartialBlocks partial : PartialBlocks.values()) {
                    blockList.add(new GeneratedPaletteBlock(baseBlockName, baseBlockProperties, type, partial, typedBlock.deferredBlock));
                }
            }
        }

        GeneratedPaletteBlock.register(eventBus);
    }


    // ! DATAGEN !
    public void generateBlockStateModels(BlockStateProvider bsp) {
        for (GeneratedPaletteBlock b : blockList) {
            if (b.isPartial()) {
                assert b.getPartial() != null;
                b.getPartial().genBlockStateModel(bsp, b.deferredBlock.get(), b.baseName);
                continue;
            }

            if (b.getBlockType() != null) {
                b.getBlockType().genBlockStateModel(bsp, b.deferredBlock.get(), b.baseName);
                continue;
            }

            // If block registered not automatically (for e.g. when it's base block)
            PalettesBlockStateModelData.vanillaBlockStateModel(bsp, b.deferredBlock.get(), b.baseName);
        }
    }

    public void generateLootTables(ModBlockLootTableProvider blp) {
        for (GeneratedPaletteBlock b : blockList) {
            Block block = b.deferredBlock.get();
            // as far as I can remember slabs are only block that must drops with tuple
            if (block instanceof SlabBlock) blp.pAdd(block, blp.pCreateSlabItemTable(block));
            else blp.pDropSelf(block);
        }
    }

    public void generateTags(ModBlockTagProvider btp) {
        for(GeneratedPaletteBlock b : blockList) {
            var block = b.deferredBlock.get();
            if (this.minableWithPickaxe) btp.pTag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

            if (b.isPartial()) {
                assert b.getPartial() != null;
                b.getPartial().generateTag(btp, block);
            }
        }

    }

    public void generateRecipes(ModRecipeProvider mrp, RecipeOutput out) {
        // for future if we had more ways to craft, because palettes are universal, and can be not only for stones
        generatePartialsRecipes(mrp, out);
        if (stoneCuttable) generateStonecutterRecipes(mrp, out);
    }

    private void generatePartialsRecipes(ModRecipeProvider mrp, RecipeOutput out) {
        for (GeneratedPaletteBlock result : blockList) {
            if (!result.isPartial()) continue;

            // if this is partial (and its partial, look higher for 1 line), then we always have these two
            assert result.getPartialParentBlock() != null;
            assert result.getPartial() != null;

            var partialBlock = result.deferredBlock.get();
            var parentBlock = result.getPartialParentBlock().get();
            var parentBlockKey = BuiltInRegistries.BLOCK.getKey(parentBlock);
            var advancementName = "has_" + parentBlockKey.getPath() + "_block";

            result.getPartial().generateRecipe(mrp, out, partialBlock, parentBlock, advancementName);
        }
    }
    private void generateStonecutterRecipes(ModRecipeProvider mrp,RecipeOutput out) {
        for (GeneratedPaletteBlock result : blockList) {
            for (GeneratedPaletteBlock material : blockList) {
                var resultBlock = result.deferredBlock.get();
                var materialBlock = material.deferredBlock.get();

                if (materialBlock == resultBlock) continue; // We don't need recipe to get blockA from blockA
                if (materialBlock instanceof SlabBlock) continue; // You cannot make something from slabs in stonecutter

                var materialId = BuiltInRegistries.BLOCK.getKey(materialBlock);
                var resultId = BuiltInRegistries.BLOCK.getKey(resultBlock);
                var recipeId = NeedMoreBulletsMod.asResource("stonecutting_" + materialId.getPath() + "_to_" + resultId.getPath());
                var advancementName = "has_" + materialId.getPath() + "_block";

                // if you're making slabs you should get 2 slabs
                var count = resultBlock instanceof SlabBlock ? 2 : 1;
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(materialBlock), RecipeCategory.BUILDING_BLOCKS, resultBlock, count)
                        .unlockedBy(advancementName, mrp.pHas(materialBlock.asItem()))
                        .save(out, recipeId);
            }
        }
    }
}
