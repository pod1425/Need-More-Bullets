package net.pod.cnmb.block.pallete;

import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public class PaletteBlockStateModelData {

    private static ResourceLocation texture(Block block, String baseName, String textureName) {
        return BuiltInRegistries.BLOCK.getKey(block).withPath("block/palettes/" + baseName + "/" + textureName);
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public static void defaultBlockStateModel(BlockStateProvider provider, Block block, String baseName) {
        String name = name(block);

        if (block instanceof RotatedPillarBlock)
            pillarModels(provider, block, texture(block, baseName, name + "_side"), texture(block, baseName, name + "_end"));
        else provider.simpleBlockWithItem(block, provider.models().cubeAll(name, texture(block, baseName, name)));
    }

    public static void layeredBlockStateModel(BlockStateProvider provider, Block block, String baseName) {
        String name = name(block);

        var side = texture(block, baseName, baseName + "_cap");
        var end = texture(block, baseName, name);

        provider.simpleBlockWithItem(block, provider.models().cubeColumn(name, end, side));
    }

    public static void pillarBlockStateModel(BlockStateProvider provider, Block block, String baseName) {
        pillarModels(provider, block, texture(block, baseName, name(block)), texture(block, baseName, baseName + "_cap"));
    }

    private static void pillarModels(BlockStateProvider provider, Block block, ResourceLocation side, ResourceLocation end) {
        String name = name(block);

        var model = provider.models().cubeColumn(name, side, end);
        var horizontal = provider.models().cubeColumnHorizontal(name + "_horizontal", side, end);

        provider.itemModels().getBuilder(name).parent(model);

        provider.getVariantBuilder(block).forAllStatesExcept(state -> {
                    Direction.Axis axis = state.getValue(ConnectedPillarBlock.AXIS);

                    return ConfiguredModel.builder()
                            .modelFile(axis == Direction.Axis.Y ? model : horizontal)
                            .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                            .rotationY(axis == Direction.Axis.X ? 90 : 0)
                            .uvLock(false).build();

                }, BlockStateProperties.WATERLOGGED,
                ConnectedPillarBlock.NORTH,
                ConnectedPillarBlock.SOUTH,
                ConnectedPillarBlock.EAST,
                ConnectedPillarBlock.WEST);
    }

    public static void slabBlockStateModel(BlockStateProvider provider, Block block, String baseName) {
        String name = name(block);
        String baseTextureName = name.replace("_slab", "");

        var texture = texture(block, baseName, baseTextureName);
        var doubleTexture = texture(block, baseName, name);

        var models = provider.models();

        var bottom = models.slab(name, texture, texture, texture);
        var top = models.slabTop(name + "_top", texture, texture, texture);

        var doubleModel = models.cubeAll(name + "_double",
                models.existingFileHelper.exists(doubleTexture, PackType.CLIENT_RESOURCES, ".png", "textures") ? doubleTexture : texture);

        provider.itemModels().getBuilder(name).parent(bottom);

        provider.getVariantBuilder(block).forAllStates(state -> switch (state.getValue(SlabBlock.TYPE)) {
            case BOTTOM -> ConfiguredModel.builder().modelFile(bottom).build();
            case TOP -> ConfiguredModel.builder().modelFile(top).build();
            case DOUBLE -> ConfiguredModel.builder().modelFile(doubleModel).build();
        });
    }

    public static void stairsBlockStateModel(BlockStateProvider provider, Block block, String baseName) {
        String name = name(block);
        var texture = texture(block, baseName, name.replace("_stairs", ""));

        provider.stairsBlock((StairBlock) block, texture);
        provider.itemModels().stairs(name, texture, texture, texture);
    }

    public static void wallBlockStateModel(BlockStateProvider provider, Block block, String baseName) {
        String name = name(block);
        var texture = texture(block, baseName, name.replace("_wall", ""));

        provider.wallBlock((WallBlock) block, texture);
        provider.itemModels().wallInventory(name, texture);
    }
}