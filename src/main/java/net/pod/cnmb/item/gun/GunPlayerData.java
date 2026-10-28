package net.pod.cnmb.item.gun;

import net.pod.cnmb.networking.ModNetworking;

public class GunPlayerData {

    private boolean triggerPressed;
    private ModNetworking.GunHand triggerHand;
    private boolean shotOccurred;

    public boolean isTriggerPressed() {
        return triggerPressed;
    }

    public void setTriggerPressed(boolean triggerPressed) {
        this.triggerPressed = triggerPressed;
    }

    public ModNetworking.GunHand getTriggerHand() {
        return triggerHand;
    }

    public void setTriggerHand(ModNetworking.GunHand triggerHand) {
        this.triggerHand = triggerHand;
    }

    public boolean hasShotOccurred() {
        return shotOccurred;
    }

    public void setShotOccurred(boolean shotOccurred) {
        this.shotOccurred = shotOccurred;
    }
}