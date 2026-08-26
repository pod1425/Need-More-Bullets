package net.pod.cnmb.palettes;

import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public class ModelGenerator {
    @FunctionalInterface
    interface IBlockStateProvider extends NonNullBiConsumer<DataGenContext<Block, Block>, RegistrateBlockstateProvider> {}

    public static IBlockStateProvider cubeAll(String name, ResourceLocation texture) {
        return (ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
                .cubeAll(name, texture));
    }

//    public CNMBPaletteBlockPattern.IBlockStateProvider cubeBottomTop(String variant) {
//        ResourceLocation side = toLocation(variant, textures[0]);
//        ResourceLocation bottom = toLocation(variant, textures[1]);
//        ResourceLocation top = toLocation(variant, textures[2]);
//        return (ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
//                .cubeBottomTop(createName(variant), side, bottom, top));
//    }

    public static IBlockStateProvider pillar(String name, ResourceLocation sideTexture, ResourceLocation endTexture) {
        return (ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                            if (axis == Direction.Axis.Y)
                                return ConfiguredModel.builder()
                                        .modelFile(prov.models()
                                                .cubeColumn(name, sideTexture, endTexture))
                                        .uvLock(false)
                                        .build();
                            return ConfiguredModel.builder()
                                    .modelFile(prov.models()
                                            .cubeColumnHorizontal(name + "_horizontal", sideTexture, endTexture))
                                    .uvLock(false)
                                    .rotationX(90)
                                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                    .build();
                        }, BlockStateProperties.WATERLOGGED, ConnectedPillarBlock.NORTH, ConnectedPillarBlock.SOUTH,
                        ConnectedPillarBlock.EAST, ConnectedPillarBlock.WEST);
    }

//    public CNMBPaletteBlockPattern.IBlockStateProvider cubeColumn(String variant) {
//        ResourceLocation side = toLocation(variant, textures[0]);
//        ResourceLocation end = toLocation(variant, textures[1]);
//        return (ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
//                .cubeColumn(createName(variant), side, end));
//    }
}
