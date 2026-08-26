package net.pod.cnmb.palettes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Palette {
    private final List<BlockTypes> types = new ArrayList<>();
    public final List<GeneratedPaletteBlock> blockList = new ArrayList<>();

    private final String baseBlockName;
    private final BlockBehaviour.Properties baseBlockProperties;

    public Palette(String baseBlockName, BlockBehaviour.Properties baseBlockProperties) {
        this.baseBlockName = baseBlockName;
        this.baseBlockProperties = baseBlockProperties;
    }

    public Palette add(BlockTypes type) {
        types.add(type);
        return this;
    }

    public Palette add(BlockTypes[] types) {
        this.types.addAll(List.of(types));
        return this;
    }

    public DeferredBlock<? extends Block> getBaseBlock() {
        var block = blockList.stream().filter(b -> Objects.equals(b.finalName, baseBlockName)).findFirst().orElseThrow();
        return block.deferredBlock;
    }

    public void register(IEventBus eventBus) {
        // Don't forget to register base block
        blockList.add(new GeneratedPaletteBlock(baseBlockName, baseBlockProperties));

        for (BlockTypes type : types) {
            var typedBlock = new GeneratedPaletteBlock(baseBlockName, baseBlockProperties, type);
            blockList.add(typedBlock);
            if (type.needPartialsRegister()) {
                for(PartialBlocks partial : PartialBlocks.values()) {
                    blockList.add(new GeneratedPaletteBlock(baseBlockName, baseBlockProperties, type, partial, typedBlock.deferredBlock));
                }
            }
        }

        GeneratedPaletteBlock.register(eventBus);
    }
}
