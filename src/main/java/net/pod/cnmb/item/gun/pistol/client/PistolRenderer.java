package net.pod.cnmb.item.gun.pistol.client;

import net.pod.cnmb.item.gun.pistol.PistolItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PistolRenderer extends GeoItemRenderer<PistolItem> {
    public PistolRenderer() {
        super(new PistolModel());
    }
}
