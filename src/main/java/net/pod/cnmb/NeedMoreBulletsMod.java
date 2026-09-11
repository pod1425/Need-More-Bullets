package net.pod.cnmb;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.registry.*;
import org.slf4j.Logger;

@Mod(NeedMoreBulletsMod.MODID)
public class NeedMoreBulletsMod {
    public static final String MODID = "cnmb";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NeedMoreBulletsMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockPalettes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(ModNetworking::register);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModSounds.register(modEventBus);
    }
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
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
