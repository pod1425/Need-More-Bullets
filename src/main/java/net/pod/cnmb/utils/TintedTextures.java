package net.pod.cnmb.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TintedTextures {
    public static boolean createTinted(ResourceLocation source, ResourceLocation target, int tintArgb) {
        Minecraft mc = Minecraft.getInstance();

        Optional<Resource> res = mc.getResourceManager().getResource(source);
        if (res.isEmpty()) return false;

        try (InputStream in = res.get().open()) {
            NativeImage src = NativeImage.read(in);

            int ta = tintArgb >>> 24 & 0xFF;
            int tr = tintArgb >> 16 & 0xFF;
            int tg = tintArgb >> 8 & 0xFF;
            int tb = tintArgb & 0xFF;

            for (int y = 0; y < src.getHeight(); y++) {
                for (int x = 0; x < src.getWidth(); x++) {
                    int px = src.getPixelRGBA(x, y);   // в 1.21.1 формат ABGR
                    int a = px >>> 24 & 0xFF;
                    int b = px >> 16 & 0xFF;
                    int g = px >> 8 & 0xFF;
                    int r = px & 0xFF;

                    a = a * ta / 255;
                    r = r * tr / 255;
                    g = g * tg / 255;
                    b = b * tb / 255;

                    src.setPixelRGBA(x, y, a << 24 | b << 16 | g << 8 | r);
                }
            }

            DynamicTexture tex = new DynamicTexture(src);
            mc.getTextureManager().register(target, tex);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
