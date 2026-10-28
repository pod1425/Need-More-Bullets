package net.pod.cnmb.item.gun.musket.client;

import net.pod.cnmb.item.gun.musket.MusketItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MusketRenderer extends GeoItemRenderer<MusketItem> {
    public MusketRenderer() {
        super(new MusketModel());
    }
}
