package net.pod.cnmb.block.pallete;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Palette {
    public final String name;
    private ToolRequirement toolRequirement = ToolRequirement.NONE;
    public final DeferredBlock<Block> deferred;
    private final List<PaletteBlock> paletteBlocks = new ArrayList<>();

    public Palette(DeferredRegister.Blocks register, String name, List<PaletteType> types,
                   BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, Block> supplier) {
        this.name = name;

        var basePaletteBlock = new PaletteBlock(register, supplier, properties, name, null);
        deferred = basePaletteBlock.deferred;

        paletteBlocks.add(basePaletteBlock);
        for (PaletteType type : types) paletteBlocks.add(new PaletteBlock(register, properties, name, type));
    }

    public List<PaletteBlock> get() {
        return paletteBlocks;
    }
    public PaletteBlock getType(PaletteType type) {
        return paletteBlocks.stream().filter(paletteBlock -> paletteBlock.type == type).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Palette type not found: " + type));
    }

    public ToolRequirement getToolRequirement() {
        return toolRequirement;
    }
    public Palette setToolRequirement(ToolRequirement tool) {
        toolRequirement = tool;
        return this;
    }

    public void register() {

    }
}