package net.pod.cnmb.util;

import net.minecraft.world.phys.Vec3;

public class VecUtils {
    public static Vec3 add(Vec3 v, double n) {
        return v.add(n, n, n);
    }
    public static Vec3 sub(Vec3 v, double n) {
        return v.subtract(n, n, n);
    }
}
