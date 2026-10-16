package net.pod.cnmb.block.pallet;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.pod.cnmb.registry.ModBlockEntities;

import java.util.List;

public class PalletBlockEntity extends SmartBlockEntity implements Clearable {
    private ItemStack heldItem = ItemStack.EMPTY;

    public PalletBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PALLET.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(
                new DirectBeltInputBehaviour(this)
                        .setInsertionHandler(this::tryInserting)
                        .considerOccupiedWhen(p -> !heldItem.isEmpty())
        );
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!heldItem.isEmpty())
            tag.put("HeldItem", heldItem.save(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        heldItem = tag.contains("HeldItem") ? ItemStack.parse(registries, tag.getCompound("HeldItem")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void destroy() {
        Block.popResource(level, worldPosition, heldItem);
    }

    @Override
    public void clearContent() {
        heldItem = ItemStack.EMPTY;
        setChangedAndUpdate();
    }

    public void setHeldItem(ItemStack heldItem) {
        this.heldItem = heldItem;
        setChangedAndUpdate();
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    private ItemStack tryInserting(TransportedItemStack stack, Direction side, boolean simulate) {
        var item = stack.stack;

        if (!heldItem.isEmpty())
            return item;
        if (!simulate)
            setHeldItem(item);
        return ItemStack.EMPTY;
    }

    private void setChangedAndUpdate() {
        setChanged();

        if (level == null || level.isClientSide)
            return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);

        level.updateNeighborsAt(worldPosition, state.getBlock());
        level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
    }
}
