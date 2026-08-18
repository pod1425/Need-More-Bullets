package net.pod.cnmb.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.VariableLampBlock;
import net.pod.cnmb.registry.ModBlocks;
import net.pod.cnmb.registry.ModItems;


public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NeedMoreBulletsMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.LEAD_BLOCK);
        blockWithItem(ModBlocks.CUT_STEEL);
        blockWithItem(ModBlocks.STEEL_BLOCK);
        blockWithItem(ModBlocks.RAW_LEAD_BLOCK);
        blockWithItem(ModBlocks.CUT_LEAD);
        blockWithItem(ModBlocks.STEEL_BRICKS);
        blockWithItem(ModBlocks.ACTIVE_SCULK);


        blockWithItem(ModBlocks.LEAD_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_LEAD_ORE);

        registerLamps();
    }

    private void registerLamps() {
        getVariantBuilder(ModBlocks.LEAD_LAMP.get()).forAllStates(state -> {
            String s = switch (state.getValue(VariableLampBlock.LIGHT_LEVEL)) {
                case 1,2,3,4,5 -> "lead_lamp_1";
                case 6,7,8,9,10 -> "lead_lamp_2";
                case 11,12,13,14,15 -> "lead_lamp_3";
                default -> "lead_lamp_off";
            };
            return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll(s,
                    ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + s)))};
        });
        simpleBlockItem(ModBlocks.LEAD_LAMP.get(), models().cubeAll("lead_lamp_off",
                ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + "lead_lamp_off")));

        getVariantBuilder(ModBlocks.STEEL_LAMP.get()).forAllStates(state -> {
            int st = state.getValue(VariableLampBlock.LIGHT_LEVEL);
            String s = switch (st) {
                case 1, 2, 3, 4 -> "steel_lamp_" + st;
                case 5, 6, 7, 8, 9 -> "steel_lamp_" + (st - 1);
                case 10, 11, 12, 13, 14 -> "steel_lamp_" + (st - 2);
                case 15 -> "steel_lamp_12";
                default -> "steel_lamp_off";
            };
            return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll(s,
                    ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + s)))};
        });
        simpleBlockItem(ModBlocks.STEEL_LAMP.get(), models().cubeAll("steel_lamp_off",
                ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + "steel_lamp_off")));
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
