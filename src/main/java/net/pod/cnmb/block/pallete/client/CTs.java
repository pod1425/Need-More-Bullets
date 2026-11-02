package net.pod.cnmb.block.pallete.client;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.resources.ResourceLocation;

public enum CTs {
    CAP(AllCTTypes.OMNIDIRECTIONAL, "+_cap"),
    PILLAR(AllCTTypes.RECTANGLE, "+_pillar"),
    LAYERED(AllCTTypes.HORIZONTAL_KRYPPERS, "layered_+");

    private final String textureExt;
    private final CTType type;

    CTs(CTType type, String textureExt) {
        this.textureExt = textureExt;
        this.type = type;
    }

    public CTSpriteShiftEntry get(ResourceLocation baseTexture) {
        String path = baseTexture.getPath();
        int sep = path.lastIndexOf('/');

        String dir = path.substring(0, sep + 1);
        String name = path.substring(sep + 1);

        var texture = baseTexture.withPath(dir + this.textureExt.replace("+", name));
        var connectedTexture = texture.withSuffix("_connected");
        return CTSpriteShifter.getCT(this.type, texture, connectedTexture);
    }
}