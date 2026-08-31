package net.pod.cnmb.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.palettes.Palette;
import net.pod.cnmb.registry.ModBlockPalettes;
import net.pod.cnmb.registry.ModBlocks;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NeedMoreBulletsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.LEAD_BLOCK.get())
                .add(ModBlocks.CUT_STEEL.get())
                .add(ModBlocks.STEEL_BLOCK.get())
                .add(ModBlocks.RAW_LEAD_BLOCK.get())
//                .add(ModBlocks.CUT_LEAD.get())
                .add(ModBlocks.LEAD_LAMP.get())
                .add(ModBlocks.STEEL_BRICKS.get())
                .add(ModBlocks.STEEL_LAMP.get())
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.DEEPSLATE_LEAD_ORE.get());


        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.LEAD_BLOCK.get())
                .add(ModBlocks.CUT_STEEL.get())
                .add(ModBlocks.STEEL_BLOCK.get())
                .add(ModBlocks.RAW_LEAD_BLOCK.get())
//                .add(ModBlocks.CUT_LEAD.get())
                .add(ModBlocks.LEAD_LAMP.get())
                .add(ModBlocks.STEEL_BRICKS.get())
                .add(ModBlocks.STEEL_LAMP.get())
                .add(ModBlocks.LEAD_ORE.get())
                .add(ModBlocks.DEEPSLATE_LEAD_ORE.get());


        for (Palette p : ModBlockPalettes.palettes) {
            p.generateTags(this);
        }
    }

    // well, tag() method protected, so to keep all datagen for palettes in palettes we should do it private
    // or, we can add datagen here, but then it will be split in project, because part of it you used to place it palettes classes
    // for me, it's better to place it in one place rather than doing shit with partial splitting
    public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> pTag(TagKey<Block> tag) { return tag(tag); }
}