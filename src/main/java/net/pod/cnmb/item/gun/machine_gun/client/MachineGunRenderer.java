package net.pod.cnmb.item.gun.machine_gun.client;

import net.pod.cnmb.item.gun.client.AbstractGunRenderer;
import net.pod.cnmb.item.gun.machine_gun.MachineGunItem;

public class MachineGunRenderer extends AbstractGunRenderer<MachineGunItem> {
    public MachineGunRenderer() {
        super(new MachineGunModel());
    }
}
