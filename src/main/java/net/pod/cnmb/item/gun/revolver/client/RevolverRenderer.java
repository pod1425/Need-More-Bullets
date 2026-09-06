package net.pod.cnmb.item.gun.revolver.client;


import net.pod.cnmb.item.gun.revolver.RevolverItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RevolverRenderer extends GeoItemRenderer<RevolverItem> {
    public RevolverRenderer() {
        super(new RevolverModel());
    }
}
