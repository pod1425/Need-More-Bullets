package net.pod.cnmb.palettes;

import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

@SuppressWarnings("DuplicatedCode")
public class PalettesBlockStateModelData {

    @FunctionalInterface
    public interface PalettesDataGenConsumer<T extends Block> {
        void accept(BlockStateProvider bsp, T block, String baseBlockName);
    }

    // ? Use IDE possibilities! when you fold these all functions it looks much cleaner
    // ? AI are good here, but anyway do not silly blindly copy-past!!!!

    public static <T extends Block> void defaultBlockStateModel(BlockStateProvider bsp, T block, String baseBlockName) {
        // This generator designed for palettes, so I think it's ok if I'll hardcode texture locations
        var blockKey = BuiltInRegistries.BLOCK.getKey(block);
        var blockName = blockKey.getPath();
        var texture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + blockName);

        if (!(block instanceof RotatedPillarBlock)) {
            bsp.simpleBlockWithItem(block, bsp.models().cubeAll(blockName, texture));
        } else {
            var sideTexture = texture.withSuffix("_side");
            var endTexture = texture.withSuffix("_end");

            var model = bsp.models().cubeColumn(blockName, sideTexture, endTexture);
            var modelHorizontal = bsp.models().cubeColumnHorizontal(blockName + "_horizontal", sideTexture, endTexture);
            bsp.itemModels().getBuilder(blockName).parent(model);

            bsp.getVariantBuilder(block)
                    .forAllStatesExcept(state -> {
                                Direction.Axis axis = state.getValue(ConnectedPillarBlock.AXIS);

                                if (axis == Direction.Axis.Y)
                                    return ConfiguredModel.builder()
                                            .modelFile(model)
                                            .uvLock(false)
                                            .build();
                                return ConfiguredModel.builder()
                                        .modelFile(modelHorizontal)
                                        .uvLock(false)
                                        .rotationX(90)
                                        .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                        .build();
                            }, BlockStateProperties.WATERLOGGED, ConnectedPillarBlock.NORTH, ConnectedPillarBlock.SOUTH,
                            ConnectedPillarBlock.EAST, ConnectedPillarBlock.WEST);
        }

    }

    public static <T extends Block> void slabBlockStateModel(BlockStateProvider bsp, T block, String baseBlockName) {
        // This generator designed for palettes, so I think it's ok if I'll hardcode texture locations
        var blockKey = BuiltInRegistries.BLOCK.getKey(block);
        var blockName = blockKey.getPath();
        var texture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + blockName.replace("_slab", ""));
        var doubleTexture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + blockName);

        var model = bsp.models().slab(blockName, texture, texture, texture);
        var topModel = bsp.models().slabTop(blockName + "_top", texture, texture, texture);
        final BlockModelBuilder doubleModel;
        if (bsp.models().existingFileHelper.exists(doubleTexture, PackType.CLIENT_RESOURCES, ".png", "textures")) {
            doubleModel = bsp.models().cubeAll(blockName + "_double", doubleTexture);
        } else {
            doubleModel = bsp.models().cubeAll(blockName + "_double", texture);
        }
        bsp.itemModels().getBuilder(blockName).parent(model);

        bsp.getVariantBuilder(block)
                .forAllStates(state -> {
                    var type = state.getValue(SlabBlock.TYPE);

                    return switch (type) {
                        case BOTTOM -> ConfiguredModel.builder()
                                .modelFile(model)
                                .build();

                        case TOP -> ConfiguredModel.builder()
                                .modelFile(topModel)
                                .build();

                        case DOUBLE -> ConfiguredModel.builder()
                                .modelFile(doubleModel)
                                .build();
                    };
                });
    }
    public static <T extends Block> void stairsBlockStateModel(BlockStateProvider bsp, T block, String baseBlockName) {
        // This generator designed for palettes, so I think it's ok if I'll hardcode texture locations
        var blockKey = BuiltInRegistries.BLOCK.getKey(block);
        var blockName = blockKey.getPath();
        var texture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + blockName.replace("_stairs", ""));

        bsp.stairsBlock((StairBlock) block, texture);
        bsp.itemModels().stairs(blockName, texture, texture, texture);
    }
    public static <T extends Block> void wallBlockStateModel(BlockStateProvider bsp, T block, String baseBlockName) {
        // This generator designed for palettes, so I think it's ok if I'll hardcode texture locations
        var blockKey = BuiltInRegistries.BLOCK.getKey(block);
        var blockName = blockKey.getPath();
        var texture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + blockName.replace("_wall", ""));

        bsp.wallBlock((WallBlock) block, texture);
        bsp.itemModels().wallInventory(blockName, texture);
    }

    public static <T extends Block> void pillarBlockStateModel(BlockStateProvider bsp, T block, String baseBlockName) {
        // This generator designed for palettes, so I think it's ok if I'll hardcode texture locations
        var blockKey = BuiltInRegistries.BLOCK.getKey(block);
        var blockName = blockKey.getPath();
        var sideTexture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + blockName);
        var endTexture = blockKey.withPath("block/palettes/" + baseBlockName + "/" + baseBlockName + "_cap");

        var model = bsp.models().cubeColumn(blockName, sideTexture, endTexture);
        var modelHorizontal = bsp.models().cubeColumnHorizontal(blockName + "_horizontal", sideTexture, endTexture);
        bsp.itemModels().getBuilder(blockName).parent(model);

        bsp.getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction.Axis axis = state.getValue(ConnectedPillarBlock.AXIS);

                    if (axis == Direction.Axis.Y)
                        return ConfiguredModel.builder()
                                .modelFile(model)
                                .uvLock(false)
                                .build();
                    return ConfiguredModel.builder()
                            .modelFile(modelHorizontal)
                            .uvLock(false)
                            .rotationX(90)
                            .rotationY(axis == Direction.Axis.X ? 90 : 0)
                            .build();
                }, BlockStateProperties.WATERLOGGED, ConnectedPillarBlock.NORTH, ConnectedPillarBlock.SOUTH,
                        ConnectedPillarBlock.EAST, ConnectedPillarBlock.WEST);
    }
}
