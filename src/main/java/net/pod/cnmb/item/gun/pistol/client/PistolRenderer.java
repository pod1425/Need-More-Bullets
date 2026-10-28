package net.pod.cnmb.item.gun.pistol.client;

import net.pod.cnmb.item.gun.client.AbstractGunRenderer;
import net.pod.cnmb.item.gun.pistol.PistolItem;

public class PistolRenderer extends AbstractGunRenderer<PistolItem> {
    public PistolRenderer() {
        super(new PistolModel());
    }
}
