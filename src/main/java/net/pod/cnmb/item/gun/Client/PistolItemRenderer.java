package net.pod.cnmb.item.gun.Client;

import net.pod.cnmb.item.gun.PistolItem;
import software.bernie.geckolib.model.GeoModel;
import net.pod.cnmb.item.gun.PistolItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PistolItemRenderer extends GeoItemRenderer<PistolItem> {
    public PistolItemRenderer() {
        super(new PistolItemModel());
    }
}
