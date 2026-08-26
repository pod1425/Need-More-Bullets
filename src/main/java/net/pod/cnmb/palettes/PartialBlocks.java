package net.pod.cnmb.palettes;

/**
 * Here we have all blocks that are NOT full blocks but which origins from other blocks
 */
public enum PartialBlocks {
    STAIRS("stairs"),
    WALL("stairs"),
    SLAB("stairs");

    private final String extName;

    public String parseName(String baseName) {
        return baseName + "_" + extName;
    }

    PartialBlocks(String extName) {
        this.extName = extName;
    }
}
