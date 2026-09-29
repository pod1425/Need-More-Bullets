package net.pod.cnmb.block.redstone_depleter;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.pod.cnmb.block.depleted_redstone.DepletedRedStoneBlock;
import net.pod.cnmb.block.redstone_depleter.client.RedStoneDepleterBlockRenderer;
import net.pod.cnmb.block.redstone_depleter.client.RedStoneDepleterItemRenderer;
import net.pod.cnmb.registry.ModBlockEntities;
import net.pod.cnmb.registry.ModBlocks;
import net.pod.cnmb.registry.ModItems;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class RedStoneDepleterBlockEntity extends KineticBlockEntity implements GeoBlockEntity {
    private static final int
            CAPACITY = 100000,
            ADDED_ENERGY = 8000,
            MAX_EXTRACT = 100,
            TIMER = 300;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final EnergyStorage energyStorage = new EnergyStorage(CAPACITY, ADDED_ENERGY, MAX_EXTRACT);
    private int timer = TIMER;
    private float boostAccumulator = 0.0F;

    private ItemStack item = ItemStack.EMPTY;

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_DEPLETER.get(), RedStoneDepleterBlockRenderer::new);
    }
    public static void registerRenderer(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new IClientItemExtensions() {
                    private final RedStoneDepleterItemRenderer renderer = new RedStoneDepleterItemRenderer();

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return renderer;
                    }
                },
                ModItems.REDSTONE_DEPLETER.get()
        );
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.REDSTONE_DEPLETER.get(),
                (be, d) -> new IItemHandler() {
                    @Override
                    public int getSlots() {
                        return 1;
                    }

                    @Override
                    public ItemStack getStackInSlot(int i) {
                        return be.item.copy();
                    }

                    @Override
                    public ItemStack insertItem(int i, ItemStack stack, boolean simulate) {
                        if (!be.item.isEmpty()) return stack;

                        ItemStack item = stack.copyWithCount(1);
                        if (!isItemValid(i, item)) return stack;

                        if (!simulate) {
                            be.item = item;
                            be.sync();
                        }

                        return stack.copyWithCount(stack.getCount() - 1);
                    }

                    @Override
                    public ItemStack extractItem(int slot, int amount, boolean simulate) {
                        ItemStack item = be.item.copy();
                        if (item.isEmpty() || amount <= 0) return ItemStack.EMPTY;
                        if (item.getCapability(Capabilities.EnergyStorage.ITEM).receiveEnergy(2, true) > 0) return ItemStack.EMPTY;

                        if (!simulate) {
                            be.item = ItemStack.EMPTY;
                            be.sync();
                        }

                        return item;
                    }

                    @Override
                    public int getSlotLimit(int i) {
                        return 1;
                    }

                    @Override
                    public boolean isItemValid(int i, ItemStack item) {
                        IEnergyStorage storage = item.getCapability(Capabilities.EnergyStorage.ITEM);
                        return storage != null && storage.canReceive();
                    }
                });
    }

    public RedStoneDepleterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REDSTONE_DEPLETER.get(), pos, blockState);
    }

    private void sync() {
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemInteractionResult useItemOn(Player player) {
        ItemStack hand = player.getMainHandItem().copy();

        if (hand.isEmpty()) {
            if (item.isEmpty()) return ItemInteractionResult.FAIL;

            player.setItemInHand(InteractionHand.MAIN_HAND, item.copy());
            item = ItemStack.EMPTY;
        } else {
            int count = hand.getCount() - 1;

            if (item.isEmpty()) {
                item = hand.copyWithCount(1);

                if (count == 0) hand = ItemStack.EMPTY;
                else hand.setCount(count);
                player.setItemInHand(InteractionHand.MAIN_HAND, hand);
            } else {
                if (count != 0) return ItemInteractionResult.FAIL;

                player.setItemInHand(InteractionHand.MAIN_HAND, item.copy());
                item = hand;
            }
        }
        sync();

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || !(level instanceof ServerLevel lvl)) return;

        if (level.getGameTime() % 4 == 0) sync();

        if (!item.isEmpty()) {
            IEnergyStorage storage = item.getCapability(Capabilities.EnergyStorage.ITEM);
            int energy = Math.min(storage.receiveEnergy(MAX_EXTRACT, true), energyStorage.extractEnergy(MAX_EXTRACT, true));

            if (energy > 0) {
                energyStorage.extractEnergy(energy, false);
                storage.receiveEnergy(energy, false);
            }
        }

        if (timer > 0) {
            timer--;
            boostAccumulator += Math.abs(getSpeed()) / 256 * 4;
            while (boostAccumulator >= 1.0F) {
                timer--;
                boostAccumulator--;
            }
        }

        if (timer <= 0) {
            if (energyStorage.getEnergyStored() + ADDED_ENERGY <= CAPACITY) {
                BlockPos below = getBlockPos().below();
                BlockState belowState = level.getBlockState(below);

                if (belowState.is(Blocks.REDSTONE_BLOCK)) level.setBlock(below, ModBlocks.DEPLETED_REDSTONE_BLOCK.get().defaultBlockState(), 3);
                else if (!belowState.is(ModBlocks.DEPLETED_REDSTONE_BLOCK.get()) ||
                        !(((DepletedRedStoneBlock) belowState.getBlock()).extract(lvl, below))) return;

                energyStorage.receiveEnergy(ADDED_ENERGY, false);
                timer = TIMER;
            }
        }
    }

    @Override
    public float calculateStressApplied() {
        return 16.0F;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        loadAdditional(packet.getTag(), provider);
    }

    private String formatFE(int energy) {
        if (energy >= 1000000) return String.format(java.util.Locale.ROOT, "%.1f MFE", energy / 1000000.0);
        if (energy >= 1000) return String.format(java.util.Locale.ROOT, "%.1f kFE", energy / 1000.0);
        return energy + " FE";
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    §eEnergy: §f" + formatFE(energyStorage.getEnergyStored()) + " / " + formatFE(CAPACITY)));
        tooltip.add(Component.literal(String.format("    Timer: §f%.1fs", timer/20.0F)));

        addStressImpactStats(tooltip, calculateStressApplied());

        if (item.isEmpty()) tooltip.add(Component.literal("    Item: -"));
        else {
            IEnergyStorage storage = item.getCapability(Capabilities.EnergyStorage.ITEM);

            tooltip.add(Component.literal("    Item: " + item.getHoverName().getString()));
            tooltip.add(Component.literal("        Energy: " + formatFE(storage.getEnergyStored()) + " / " + formatFE(storage.getMaxEnergyStored())));
        }

        return true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
        tag.put("EnergyStorage", energyStorage.serializeNBT(provider));
        tag.putInt("Timer", timer);
        if (!item.isEmpty()) tag.put("Item", item.save(provider));

        super.write(tag, provider, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
        if (tag.contains("EnergyStorage")) energyStorage.deserializeNBT(provider, tag.get("EnergyStorage"));
        if (tag.contains("Timer")) timer = tag.getInt("Timer");
        item = tag.contains("Item") ? ItemStack.parse(provider, tag.get("Item")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;

        super.read(tag, provider, clientPacket);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
