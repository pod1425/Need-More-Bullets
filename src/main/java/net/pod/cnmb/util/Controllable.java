package net.pod.cnmb.util;

import net.pod.cnmb.networking.payload.ControlInputPayload;

public interface Controllable {
    void handleInput(ControlInputPayload input);
}
