package net.pod.cnmb.palettes;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.block.connected.CTModel;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.registry.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GeneratedPaletteBlock {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NeedMoreBulletsMod.MODID);
    private static final List<GeneratedPaletteBlock> blockList = new ArrayList<>();

    public final String baseName;
    private @Nullable BlockTypes blockType = null;
    private @Nullable PartialBlocks partial = null;
    private @Nullable DeferredBlock<? extends Block> partialParentBlock = null;

    public final String finalName;
    public final DeferredBlock<? extends Block> deferredBlock;


    public GeneratedPaletteBlock(String baseName, Supplier<Block> block) {
        this.baseName = baseName;
        this.finalName = baseName;
        this.deferredBlock = registerBlock(baseName, block);

        blockList.add(this);
    }

    public GeneratedPaletteBlock(String name, BlockBehaviour.Properties blockProperties, BlockTypes blockType) {
        this.baseName = name;
        this.blockType = blockType;
        this.finalName = blockType.parseName(name);
        this.deferredBlock = registerBlock(finalName, blockType.makeBlock(blockProperties));

        blockList.add(this);
    }

    @SuppressWarnings("NullableProblems")
    public GeneratedPaletteBlock(String name, BlockBehaviour.Properties blockProperties, BlockTypes blockType, PartialBlocks partial, DeferredBlock<? extends Block> parentBlock) {
        this.baseName = name;
        this.blockType = blockType;
        this.partial = partial;
        this.partialParentBlock = parentBlock;
        this.finalName = partial.parseName(blockType.parseName(name));
        this.deferredBlock = registerBlock(finalName, () -> partial.makeBlock(parentBlock.get().defaultBlockState(), blockProperties));

        blockList.add(this);
    }

    private DeferredBlock<Block> registerBlock(String name, Supplier<? extends Block> block) {
        DeferredBlock<Block> b = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(b.get(), new Item.Properties()));
        return b;
    }

    public @Nullable BlockTypes getBlockType() {
        return this.blockType;
    }

    public Boolean isPartial() {
        return this.partial != null;
    }
    public @Nullable PartialBlocks getPartial() {
        return this.partial;
    }
    public @Nullable DeferredBlock<? extends Block> getPartialParentBlock() {
        return this.partialParentBlock;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        for (GeneratedPaletteBlock block : blockList) {
            if (block.blockType == null) continue;
            if (!block.blockType.needCTRegister()) continue;


            var texture = NeedMoreBulletsMod.asResource("block/palettes/" + block.baseName + "/" + block.baseName);
            var textureBehaviour = block.blockType.getTextureBehaviour(texture);

            eventBus.addListener((net.neoforged.neoforge.registries.RegisterEvent event) -> {
                if (!event.getRegistryKey().equals(Registries.BLOCK)) {
                    return;
                }

                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> CreateClient.MODEL_SWAPPER.getCustomBlockModels()
                        .register(NeedMoreBulletsMod.asResource(block.finalName), model -> new CTModel(model, textureBehaviour)));
            });
        }
    }

}
