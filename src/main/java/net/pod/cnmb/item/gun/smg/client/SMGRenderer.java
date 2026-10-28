package net.pod.cnmb.item.gun.smg.client;

import net.pod.cnmb.item.gun.client.AbstractGunRenderer;
import net.pod.cnmb.item.gun.smg.SMGItem;

public class SMGRenderer extends AbstractGunRenderer<SMGItem> {
    public SMGRenderer() {
        super(new SMGModel());
    }
}
