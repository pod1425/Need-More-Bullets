package net.pod.cnmb.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pod.cnmb.NeedMoreBulletsMod;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, NeedMoreBulletsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //tag(ModTags.Items.TRANSFORMABLE_ITEMS)
        //        .add(ModItems.BISMUTH.get())
        //        .add(ModItems.RAW_BISMUTH.get())
        //        .add(Items.COAL)
        //        .add(Items.STICK)
        //        .add(Items.COMPASS);

    }
}
