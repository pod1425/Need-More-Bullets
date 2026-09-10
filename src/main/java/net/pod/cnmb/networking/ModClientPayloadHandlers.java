package net.pod.cnmb.networking;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pod.cnmb.event.ModClientEvents;
import net.pod.cnmb.networking.payload.SetControllingPayload;
import net.pod.cnmb.util.ControllableMob;

public class ModClientPayloadHandlers {
    public static void setControlling(SetControllingPayload payload, IPayloadContext context) {
        ControllableMob mob = (ControllableMob) Minecraft.getInstance().level.getEntity(payload.mobId());
        ModClientEvents.controlling = payload.controlling() ? mob : null;
        mob.controller = payload.controlling() ? Minecraft.getInstance().player : null;
    }
}
