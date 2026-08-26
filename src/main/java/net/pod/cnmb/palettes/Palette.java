package net.pod.cnmb.palettes;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.registry.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Palette {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NeedMoreBulletsMod.MODID);

    private final List<BlockTypes> types = new ArrayList<>();

    private final String baseBlockName;
    private final BlockBehaviour.Properties baseBlockProperties;

    public Palette(String baseBlockName, BlockBehaviour.Properties baseBlockProperties) {
        this.baseBlockName = baseBlockName;
        this.baseBlockProperties = baseBlockProperties;
    }

    public Palette add(BlockTypes type) {
        types.add(type);
        return this;
    }

    public Palette add(BlockTypes[] types) {
        this.types.addAll(List.of(types));
        return this;
    }

    public void register(IEventBus eventBus) {
        // Don't forget to register base block
        registerBlock(baseBlockName, () -> new Block(baseBlockProperties));

        for (BlockTypes type : types) {
            var name = type.parseName(baseBlockName);
            var block = type.makeBlock(baseBlockProperties);
            var texture = NeedMoreBulletsMod.asResource("block/palettes/" + baseBlockName + "/" + baseBlockName);
            var textureBehaviour = type.getTextureBehaviour(texture);

            var b = registerBlock(name, block);
            if (textureBehaviour != null) addCTListener(eventBus, name, textureBehaviour);


            if (type.needPartialsRegister()) {
                for(PartialBlocks partial : PartialBlocks.values()) {
                    // * For now, we register partials without any connected textures
                    // * Why? because in vanilla create there's no any partials with CT
                    var partialName = partial.parseName(name);
                    Supplier<? extends  Block> partialBlock = () -> partial.makeBlock(b.get().defaultBlockState(), baseBlockProperties);
                    registerBlock(partialName, partialBlock);
                }
            }
        }

        BLOCKS.register(eventBus);
    }

    private DeferredBlock<Block> registerBlock(String name, Supplier<? extends Block> block) {
        DeferredBlock<Block> b = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(b.get(), new Item.Properties()));
        return b;
    }

    private void addCTListener(IEventBus eventBus, String name, ConnectedTextureBehaviour textureBehaviour) {
        eventBus.addListener((net.neoforged.neoforge.registries.RegisterEvent event) -> {
            if (!event.getRegistryKey().equals(Registries.BLOCK)) {
                return;
            }

            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> CreateClient.MODEL_SWAPPER.getCustomBlockModels()
                    .register(NeedMoreBulletsMod.asResource(name), model -> new CTModel(model, textureBehaviour)));
        });
    }
}
