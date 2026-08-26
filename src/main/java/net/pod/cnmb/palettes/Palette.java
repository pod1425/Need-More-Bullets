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
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.registry.ModItems;

import java.util.ArrayList;
import java.util.List;

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
        var baseB = BLOCKS.register(baseBlockName, () -> new Block(baseBlockProperties));
        ModItems.ITEMS.register(baseBlockName, () -> new BlockItem(baseB.get(), new Item.Properties()));

        for (BlockTypes type : types) {
            var name = type.parseName(baseBlockName);
            var block = type.makeBlock(baseBlockProperties);
            var texture = NeedMoreBulletsMod.asResource("block/palettes/" + baseBlockName + "/" + baseBlockName);
            var textureBehaviour = type.getTextureBehaviour(texture);

            var b = BLOCKS.register(name, block);
            ModItems.ITEMS.register(name, () -> new BlockItem(b.get(), new Item.Properties()));
            if (textureBehaviour != null) addCTListener(eventBus, name, textureBehaviour);

            // ? temporary without partials, I need to think how to implement them with connected textures

//            if (type.needPartialsRegister()) {
//                for(PartialBlocks partial : PartialBlocks.values()) {
//                    var partialName = partial.parseName(name);
//                    Block
//                }
//            }
        }

        BLOCKS.register(eventBus);
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
