package net.pod.cnmb.palettes;

import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import com.simibubi.create.foundation.block.connected.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class contains all available block types (e.g. cut type or any other)
 * ONLY things that can variates depending on type should be defined here
 * for e.g. block state and model for datagen can be different for different types
 * but for e.g. stonecutting can't be, so you define stonecutting on palette
 * @see Palette
 * @see net.pod.cnmb.registry.ModBlockPalettes
 */
@SuppressWarnings("unused")
public class BlockTypes {
    public static final BlockTypes
        // Stones
        CUT = createType("cut_+")
                .addPartials(),

        POLISHED_CUT = createType("polished_cut_+")
                .addPartials(),

        CUT_BRICKS = createType("cut_+_bricks")
                .addPartials(),

        SMALL_BRICKS = createType("small_+_bricks")
                .addPartials(),

        LAYERED = createType("layered_+")
                .connectedTexture((t) -> new SimpleCTBehaviour(CTs.LAYERED.get(t))),

        PILLAR = createType("+_pillar")
                .block(ConnectedPillarBlock::new) // RotatedPillarBlock and ConnectedPillarBlocks it turns out are different
                .connectedTexture((t) -> new RotatedPillarCTBehaviour(CTs.PILLAR.get(t), CTs.CAP.get(t)))
                .blockStateModel(PalettesBlockStateModelData::pillarBlockStateModel),

        CHISELED = createType("chiseled_+")
                .addPartials()
    ;
        // something else you also can add here, and literally anything
        // for e.g. you can add here wood
        // + sign will be replaced with base block name

    /**
     *  prepared ranges, because nobody wants to add each of blockType to palette
     *  you also can create here other ranges, for e.g. for wood
     */
    public static final BlockTypes[] PREPARED_VANILLA_STONE_RANGE = { CUT, POLISHED_CUT, CUT_BRICKS, SMALL_BRICKS, LAYERED, PILLAR };
    public static final BlockTypes[] PREPARED_EXTENDED_STONE_RANGE = { CUT, POLISHED_CUT, CUT_BRICKS, SMALL_BRICKS, LAYERED, PILLAR, CHISELED };

    private final String ext;
    private Boolean hasPartials = false;
    private Function<BlockBehaviour.Properties, ? extends Block> blockFactory = Block::new;
    private @Nullable Function<ResourceLocation, ConnectedTextureBehaviour> textureFactory;
    private PalettesBlockStateModelData.PalettesDataGenConsumer<? super Block> blockStateModelDatagenFactory = PalettesBlockStateModelData::defaultBlockStateModel;


    private BlockTypes(String ext) {
        this.ext = ext;
    }

    public String parseName(String baseName) {
        return ext.replace("+", baseName);
    }

    public Boolean hasPartials() {
        return this.hasPartials;
    }

    public Supplier<? extends Block> makeBlock(BlockBehaviour.Properties p) {
        return () -> blockFactory.apply(p);
    }

    public ConnectedTextureBehaviour getTextureBehaviour(ResourceLocation t) {
        assert textureFactory != null;
        return textureFactory.apply(t);
    }
    public Boolean needCTRegister() { return this.textureFactory != null; }

    public <T extends Block> void genBlockStateModel(BlockStateProvider bsp, T b, String bbn) {
        this.blockStateModelDatagenFactory.accept(bsp, b, bbn);
    }

    /**
     * If you add partials for entry it will add such blocks
     * as stairs, slabs, walls...
     * @see PartialBlocks
     */
    private BlockTypes addPartials() {
        this.hasPartials = true;
        return this;
    }

    /**
     * Needs when you need any other block rather than default
     * Often needs when you want connected textures
     * E.g:
     * @snippet
     *  createType("pillar")
     *      .block(RotatedPillarBlock::new)
     */
    private BlockTypes block(Function<BlockBehaviour.Properties, ? extends Block> blockFactory) {
        this.blockFactory = blockFactory;
        return this;
    }

    /**
     * If you want to add Connected texture for type, you need connected texture behavior
     * you can define it here
     * @snippet
     *  createType("layered_+")
     *      .connectedTexture((t) -> new SimpleCTBehaviour(CTs.LAYERED.get(t)))
     */
    private BlockTypes connectedTexture(Function<ResourceLocation, ConnectedTextureBehaviour> textureFactory) {
        this.textureFactory = textureFactory;
        return this;
    }

    /**
     * Here you can specify datagenerator for blocks of this type
     * by default it PalettesBlockStateModelData.vanillaBlockStateModel
     * @see PalettesBlockStateModelData
     * @snippet
     *  createType("pillar")
     *      .blockStateModel(PalettesBlockStateModelData::pillarBlockStateModel)
     */
    private BlockTypes blockStateModel(PalettesBlockStateModelData.PalettesDataGenConsumer<? super Block> blockStateModelDatagenFactory) {
        this.blockStateModelDatagenFactory = blockStateModelDatagenFactory;
        return this;
    }

    private static BlockTypes createType(String ext) {
        return new BlockTypes(ext);
    }

    private enum CTs {
        CAP(AllCTTypes.OMNIDIRECTIONAL, "+_cap"),
        PILLAR(AllCTTypes.RECTANGLE, "+_pillar"),
        LAYERED(AllCTTypes.HORIZONTAL_KRYPPERS, "layered_+")
        ;

        private final String textureExt;
        private final CTType type;

        CTs(CTType type, String textureExt) {
            this.textureExt = textureExt;
            this.type = type;
        }

        public CTSpriteShiftEntry get(ResourceLocation baseTexture) {
            var texture = baseTexture.withPath(this.textureExt.replace("+", baseTexture.getPath()));
            var connectedTexture = texture.withSuffix("_connected");
            return CTSpriteShifter.getCT(this.type, texture, connectedTexture);
        }
    }

}
