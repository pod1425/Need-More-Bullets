package net.pod.cnmb.palettes;

import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import com.simibubi.create.foundation.block.connected.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockTypes {
    public static final BlockTypes
        // Stones
        CUT = createType("cut_+")
                .addPartials(),

        POLISHED = createType("polished_+")
                .addPartials(),

        BRICKS = createType("+_bricks")
                .addPartials(),

        SMALL_BRICKS = createType("small_+_bricks")
                .addPartials(),

        LAYERED = createType("layered_+")
                .connectedTexture((t) -> new SimpleCTBehaviour(CTs.LAYERED.get(t))),

        PILLAR = createType("+_pillar")
                .block(ConnectedPillarBlock::new) // RotatedPillarBlock and ConnectedPillarBlocks it turns out are different
                .connectedTexture((t) -> new RotatedPillarCTBehaviour(CTs.PILLAR.get(t), CTs.CAP.get(t)));

        // something else you also can add here, and literally anything
        // for e.g. you can add here wood
        // + sign will be replaced with base block name

    /**
     *  prepared ranges, because nobody wants to add each of blockType to palette
     *  you also can create here other ranges, for e.g for wood
     */
    public static final BlockTypes[] PREPARED_VANILLA_STONE_RANGE = { CUT, POLISHED, BRICKS, SMALL_BRICKS, LAYERED, PILLAR };

    private final String ext;
    private Boolean hasPartials;
    private Function<BlockBehaviour.Properties, ? extends Block> blockFactory = Block::new;
    private @Nullable Function<ResourceLocation, ConnectedTextureBehaviour> textureFactory;


    private BlockTypes(String ext) {
        this.ext = ext;
    }

    public String parseName(String baseName) {
        return ext.replace("+", baseName);
    }

    public Boolean needPartialsRegister() {
        return this.hasPartials;
    }

    public Supplier<? extends Block> makeBlock(BlockBehaviour.Properties p) {
        return () -> blockFactory.apply(p);
    }

    public @Nullable ConnectedTextureBehaviour getTextureBehaviour(ResourceLocation t) {
        if (textureFactory == null) return  null;
        return textureFactory.apply(t);
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
