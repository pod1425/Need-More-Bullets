package net.pod.cnmb.utils;

public record ArmTransform(float x, float y, float z, float rotX, float rotY, float rotZ) {
    public static final ArmTransform ZERO = new ArmTransform(0, 0, 0, 0, 0, 0);
}