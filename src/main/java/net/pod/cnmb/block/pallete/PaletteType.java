package net.pod.cnmb.block.pallete;

import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.pod.cnmb.block.pallete.client.CTs;

import java.util.function.Function;

public class PaletteType {
    public static final PaletteType
            CUT = new PaletteType("cut_+", true),
            POLISHED_CUT = new PaletteType("polished_cut_+", true),
            CUT_BRICKS = new PaletteType("cut_+_bricks", true),
            SMALL_BRICKS = new PaletteType("small_+_bricks", true),
            LAYERED = new PaletteType("layered_+", false, t -> new HorizontalCTBehaviour(CTs.LAYERED.get(t), CTs.CAP.get(t)))
                    .setDatagenFactory(PaletteBlockStateModelData::layeredBlockStateModel),
            PILLAR = new PaletteType("+_pillar", false, t -> new HorizontalCTBehaviour(CTs.PILLAR.get(t), CTs.CAP.get(t)))
                    .setBlockFactory(ConnectedPillarBlock::new)
                    .setDatagenFactory(PaletteBlockStateModelData::pillarBlockStateModel),
            CHISELED = new PaletteType("chiseled_+", true);

    public static final PaletteType[] VANILLA_STONE_TYPES = {CUT, POLISHED_CUT, CUT_BRICKS, SMALL_BRICKS, LAYERED, PILLAR};
    public static final PaletteType[] EXTENDED_STONE_TYPES = {CUT, POLISHED_CUT, CUT_BRICKS, SMALL_BRICKS, LAYERED, PILLAR, CHISELED};

    final String type;
    final boolean hasPartials;
    private final Function<ResourceLocation, ConnectedTextureBehaviour> connectedTextureFactory;
    private Function<BlockBehaviour.Properties, ? extends Block> blockFactory = Block::new;
    private PaletteBlockPartial.DataGenConsumer<Block> datagenFactory = PaletteBlockStateModelData::defaultBlockStateModel;

    PaletteType(String type, boolean hasPartials) {
        this(type, hasPartials, null);
    }

    PaletteType(String type, boolean hasPartials, Function<ResourceLocation, ConnectedTextureBehaviour> connectedTextureFactory) {
        this.type = type;
        this.hasPartials = hasPartials;
        this.connectedTextureFactory = connectedTextureFactory;
    }

    public PaletteType setBlockFactory(Function<BlockBehaviour.Properties, ? extends Block> blockFactory) {
        this.blockFactory = blockFactory;
        return this;
    }
    public PaletteType setDatagenFactory(PaletteBlockPartial.DataGenConsumer<Block> datagenFactory) {
        this.datagenFactory = datagenFactory;
        return this;
    }

    public Block getBlock(BlockBehaviour.Properties properties) {
        return blockFactory.apply(properties);
    }
    public <T extends Block> void genBlockStateModel(BlockStateProvider bsp, T b, String bbn) {
        datagenFactory.accept(bsp, b, bbn);
    }

    public String get(String name) {
        return type.replace("+", name);
    }

    public Function<ResourceLocation, ConnectedTextureBehaviour> getConnectedTexture() {
        return connectedTextureFactory;
    }

    public boolean hasPartials() {
        return hasPartials;
    }
}