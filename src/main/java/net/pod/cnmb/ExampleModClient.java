package net.pod.cnmb;

import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.foundation.block.connected.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.pod.cnmb.entity.projectile.GenericBulletRenderer;
import net.pod.cnmb.registry.ModEntities;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static net.pod.cnmb.registry.ModBlocks.CERUSSITE_PILLAR;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = NeedMoreBulletsMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = NeedMoreBulletsMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {

    private static void registerCT(Block block, CTSpriteShiftEntry entry) {
        connectedTextures(() -> new SimpleCTBehaviour(entry)).accept(block);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(
                    ModEntities.GENERIC_BULLET.get(),
                    GenericBulletRenderer::new
            );
        });

        //connectedTextures(v -> new RotatedPillarCTBehaviour(ct(v, PaletteBlockPattern.CTs.PILLAR), ct(v, PaletteBlockPattern.CTs.CAP)));

        registerCT(CERUSSITE_PILLAR.get(), CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL,
                ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + "cerussite_cut_cap"),
                ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + "cerussite_cut_cap_connected")
        ));
        registerCT(CERUSSITE_PILLAR.get(), CTSpriteShifter.getCT(AllCTTypes.HORIZONTAL,
                ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + "cerussite_cut_pillar"),
                ResourceLocation.fromNamespaceAndPath(NeedMoreBulletsMod.MODID, "block/" + "cerussite_cut_pillar_connected")
        ));
    }

}
