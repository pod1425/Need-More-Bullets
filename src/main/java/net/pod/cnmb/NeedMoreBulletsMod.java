package net.pod.cnmb;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.pod.cnmb.entity.projectile.GenericBulletEntity;
import net.pod.cnmb.entity.projectile.GenericBulletRenderer;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.registry.ModBlocks;
import net.pod.cnmb.registry.ModCreativeTabs;
import net.pod.cnmb.registry.ModEntities;
import net.pod.cnmb.registry.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(NeedMoreBulletsMod.MODID)
public class NeedMoreBulletsMod {
    public static final String MODID = "cnmb";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NeedMoreBulletsMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        //EntityRenderers.register(ModEntities.GENERIC_BULLET.get(), GenericBulletRenderer::new);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(ModNetworking::register);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("CNMB started. (insert 120 year old engine startup sounds)");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.LEAD_INGOT);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("CNMB serverside started. (insert 120 year old engine startup sounds)");
    }
}
