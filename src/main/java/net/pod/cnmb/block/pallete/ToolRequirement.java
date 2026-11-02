package net.pod.cnmb.block.pallete;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum ToolRequirement {
    NONE,
    PICKAXE,
    AXE;

    public TagKey<Block> getTag() {
        return switch (this) {
            case PICKAXE -> BlockTags.MINEABLE_WITH_PICKAXE;
            case AXE -> BlockTags.MINEABLE_WITH_AXE;
            default -> null;
        };
    }
}
