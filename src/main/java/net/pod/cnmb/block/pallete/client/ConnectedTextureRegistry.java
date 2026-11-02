package net.pod.cnmb.block.pallete.client;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.pallete.Palette;
import net.pod.cnmb.block.pallete.PaletteBlock;
import net.pod.cnmb.registry.ModPalettes;

import java.util.function.Function;

public class ConnectedTextureRegistry {
    public static void register(IEventBus eventBus) {
        eventBus.addListener(ConnectedTextureRegistry::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.BLOCK))
            return;

        for (Palette palette : ModPalettes.PALETTES) {
            for (PaletteBlock block : palette.get()) {
                if (block.type == null) continue;

                Function<ResourceLocation, ConnectedTextureBehaviour> factory = block.type.getConnectedTexture();
                if (factory == null) continue;

                ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/palettes/" + palette.name + "/" + palette.name);
                ConnectedTextureBehaviour behaviour = factory.apply(texture);

                CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(block.deferred.getId(), model -> new CTModel(model, behaviour));
            }
        }
    }
}