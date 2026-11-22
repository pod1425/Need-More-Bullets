package net.pod.cnmb.entity.beedrone.client;

import net.minecraft.resources.ResourceLocation;
import net.pod.cnmb.NeedMoreBulletsMod;

public enum BeeDroneTextures {
    MAIN("bee_drone"),
    BATTERY0("battery_0"), BATTERY1("battery_1"), BATTERY2("battery_2"), BATTERY3("battery_3"), BATTERY4("battery_4"),
    CAMERA_TL("camera_tl"), CAMERA_TR("camera_tr"), CAMERA_BL("camera_bl"), CAMERA_BR("camera_br");

    public final ResourceLocation res;
    BeeDroneTextures(String name) {
        res = NeedMoreBulletsMod.asResource("textures/entity/bee_drone/" + name + ".png");
    }

    public static ResourceLocation getBattery(int energy) {
        if (energy < 0) return BATTERY0.res;
        if (energy > 4) return BATTERY4.res;

        BeeDroneTextures tex = switch (energy) {
            case 0 -> BATTERY0;
            case 1 -> BATTERY1;
            case 2 -> BATTERY2;
            case 3 -> BATTERY3;
            default -> BATTERY4;
        };

        return tex.res;
    }
}