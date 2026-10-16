package net.pod.cnmb.registry;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.block.pallet.PalletBlockArmInteractionType;

public class ModArmInteractionPoints {
    private static void registerAll() {
        registerAll("pallet", new PalletBlockArmInteractionType());
    }

    private static <T extends ArmInteractionPointType> T registerAll(String name, T type) {
        return Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, NeedMoreBulletsMod.asResource(name), type);
    }

    public static void registerAll(IEventBus eventBus) {
        eventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistry() == CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE) {
                registerAll();
            }
        });
    }
}
