package net.pod.cnmb.registry;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;

import static net.pod.cnmb.NeedMoreBulletsMod.REGISTRATE;
import static net.pod.cnmb.registry.CNMBAllPaletteStoneTypes.CERUSSITE;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NeedMoreBulletsMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TAB.register("main_blocks_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.STEEL_INGOT.get()))
                    .title(Component.literal("Сreate Needs More Bullets"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (Item i : ModItems.getItems()) {
                            output.accept(i);
                        }
                        for (Block b : ModBlocks.getBlocks()) {
                            output.accept(b);
                        }
                    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_TAB = CREATIVE_MODE_TAB.register("decorative_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(CERUSSITE.getVariants().registeredBlocks.getFirst().asItem()))
                    .title(Component.literal("Сreate Needs More Bullets Blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (RegistryEntry<Block, ?> block : REGISTRATE.getAll(Registries.BLOCK)) {
                            output.accept(block.get(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                        }
                        for (Block b : ModBlocks.getDecorativeBlocks()) {
                            output.accept(b);
                        }
                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}