package net.pod.cnmb.item.gun.revolver.client;

import net.pod.cnmb.item.gun.client.AbstractGunRenderer;
import net.pod.cnmb.item.gun.revolver.RevolverItem;

public class RevolverRenderer extends AbstractGunRenderer<RevolverItem> {
    public RevolverRenderer() {
        super(new RevolverModel());
    }
}
