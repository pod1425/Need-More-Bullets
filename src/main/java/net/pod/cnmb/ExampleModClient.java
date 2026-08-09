package net.pod.cnmb;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.pod.cnmb.entity.projectile.GenericBulletRenderer;
import net.pod.cnmb.registry.ModEntities;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = NeedMoreBulletsMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = NeedMoreBulletsMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(
                    ModEntities.GENERIC_BULLET.get(),
                    GenericBulletRenderer::new
            );
        });
    }

}
