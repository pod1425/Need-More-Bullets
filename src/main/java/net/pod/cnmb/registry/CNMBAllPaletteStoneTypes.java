package net.pod.cnmb.registry;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.palettes.CNMBPaletteBlockPattern;
import net.pod.cnmb.palettes.CNMBPalettesVariantEntry;

import java.util.function.Function;

import static net.pod.cnmb.palettes.CNMBPaletteBlockPattern.STANDARD_RANGE;


/**
 * Copy of AllPaletteStoneTypes from create
 * @see com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes
 */
public enum CNMBAllPaletteStoneTypes {
    CERUSSITE(STANDARD_RANGE, r -> r.paletteStoneBlock("cerussite", () -> Blocks.DEEPSLATE, true, true)
            .properties(p -> p.destroyTime(2f))
            .register()),

    ;

    private final Function<CreateRegistrate, NonNullSupplier<Block>> factory;
    private CNMBPalettesVariantEntry variants;

    public NonNullSupplier<Block> baseBlock;
    public final CNMBPaletteBlockPattern[] variantTypes;
    public TagKey<Item> materialTag;

    CNMBAllPaletteStoneTypes(CNMBPaletteBlockPattern[] variantTypes, Function<CreateRegistrate, NonNullSupplier<Block>> factory) {
        this.factory = factory;
        this.variantTypes = variantTypes;
    }

    public NonNullSupplier<Block> getBaseBlock() {
        return baseBlock;
    }

    public CNMBPalettesVariantEntry getVariants() {
        return variants;
    }

    public static void register(CreateRegistrate registrate) {
        for (CNMBAllPaletteStoneTypes paletteStoneVariants : values()) {
            paletteStoneVariants.baseBlock = paletteStoneVariants.factory.apply(registrate);
            String id = Lang.asId(paletteStoneVariants.name());
            paletteStoneVariants.materialTag =
                    AllTags.optionalTag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "stone_types/" + id));
            paletteStoneVariants.variants = new CNMBPalettesVariantEntry(id, paletteStoneVariants);
        }
    }


}
