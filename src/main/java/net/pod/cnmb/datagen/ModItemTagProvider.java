package net.pod.cnmb.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.pallete.PaletteBlockPartial;
import net.pod.cnmb.registry.ModPalettes;

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

        ModPalettes.PALETTES.forEach(palette -> {
            var paletteTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, palette.name));

            var appender = tag(paletteTag);
            palette.get().forEach(paletteBlock -> {
                appender.add(paletteBlock.deferred.get().asItem());

                paletteBlock.partials.forEach((partial, deferred) -> {
                    if (partial != PaletteBlockPartial.SLAB) appender.add(deferred.get().asItem());
                });
            });
        });
    }
}
