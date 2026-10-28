package net.pod.cnmb.item.gun.musket.client;

import net.pod.cnmb.item.gun.client.AbstractGunRenderer;
import net.pod.cnmb.item.gun.musket.MusketItem;

public class MusketRenderer extends AbstractGunRenderer<MusketItem> {
    public MusketRenderer() {
        super(new MusketModel());
    }
}
