package net.pod.cnmb.item.gun.rifle.client;



import net.pod.cnmb.item.gun.rifle.RifleItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RifleRenderer extends GeoItemRenderer<RifleItem> {
    public RifleRenderer() {
        super(new RifleModel());
    }
}
