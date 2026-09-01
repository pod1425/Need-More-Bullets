package net.pod.cnmb.datagen;


import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.pod.cnmb.palettes.Palette;
import net.pod.cnmb.registry.ModBlockPalettes;
import net.pod.cnmb.registry.ModBlocks;
import net.pod.cnmb.registry.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Stream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.LEAD_BLOCK.get());
        dropSelf(ModBlocks.CUT_STEEL.get());
        dropSelf(ModBlocks.STEEL_BLOCK.get());
        dropSelf(ModBlocks.RAW_LEAD_BLOCK.get());
//        dropSelf(ModBlocks.CUT_LEAD.get());
        dropSelf(ModBlocks.LEAD_LAMP.get());
        dropSelf(ModBlocks.STEEL_BRICKS.get());
        dropSelf(ModBlocks.STEEL_LAMP.get());
        dropSelf(ModBlocks.ACTIVE_SCULK.get());



        add(ModBlocks.LEAD_ORE.get(),
                block -> createOreDrop(ModBlocks.LEAD_ORE.get(), ModItems.RAW_LEAD.get()));
        add(ModBlocks.DEEPSLATE_LEAD_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.DEEPSLATE_LEAD_ORE.get(), ModItems.RAW_LEAD.get(), 2, 5));

        for (Palette p : ModBlockPalettes.palettes) {
            p.generateLootTables(this);
        }
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return Stream.concat(
                ModBlocks.BLOCKS.getEntries().stream().map(Holder::value),
                ModBlockPalettes.getGeneratedBlocks().stream().map( b -> b.deferredBlock.get())
        ).toList();
    }

    @Override public void add(@NotNull Block block, LootTable.@NotNull Builder builder) { super.add(block, builder); }
    @Override public LootTable.@NotNull Builder createSlabItemTable(@NotNull Block block) { return super.createSlabItemTable(block); }
    @Override public void dropSelf(@NotNull Block block) { super.dropSelf(block); }
}