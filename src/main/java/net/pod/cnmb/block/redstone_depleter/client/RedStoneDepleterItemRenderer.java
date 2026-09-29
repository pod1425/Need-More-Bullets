package net.pod.cnmb.block.redstone_depleter.client;

import net.pod.cnmb.block.redstone_depleter.RedStoneDepleterItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RedStoneDepleterItemRenderer extends GeoItemRenderer<RedStoneDepleterItem> {
    public RedStoneDepleterItemRenderer() {
        super(new RedStoneDepleterModel<>());
    }
}
