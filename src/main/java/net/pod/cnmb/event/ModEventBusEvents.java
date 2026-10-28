package net.pod.cnmb.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.beedrone.BeeDrone;
import net.pod.cnmb.entity.beedrone.Client.BeeDroneModel;
import net.pod.cnmb.registry.ModEntities;

@EventBusSubscriber(modid = NeedMoreBulletsMod.MODID)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(BeeDroneModel.LAYER_LOCATION, BeeDroneModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.BEE_DRONE.get(), BeeDrone.createAttributes().build());
    }
}
