package net.pod.cnmb.block.slip_roller.client;

import net.pod.cnmb.block.slip_roller.SlipRollerItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SlipRollerItemRenderer extends GeoItemRenderer<SlipRollerItem> {
    public SlipRollerItemRenderer() {
        super(new SlipRollerModel<>());
    }
}