package net.pod.cnmb.block.pallet;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.pod.cnmb.registry.ModBlockEntities;

public class PalletBlockArmInteraction extends ArmInteractionPoint {
    public PalletBlockArmInteraction(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return Vec3.atCenterOf(pos).add(0, 0.5, 0);
    }

    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 1;
    }

    @Override
    public ItemStack insert(ArmBlockEntity arm, ItemStack stack, boolean simulate) {
        var pallet = level.getBlockEntity(pos, ModBlockEntities.PALLET.get());

        if (pallet.isEmpty())
            return stack;
        if (pallet.get().isOccupied()) {
            return stack;
        }
        if (!simulate)
            pallet.get().setHeldItem(stack);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(ArmBlockEntity arm, int slot, int amount, boolean simulate) {
        var pallet = level.getBlockEntity(pos, ModBlockEntities.PALLET.get());
        if (pallet.isEmpty())
            return ItemStack.EMPTY;
        var item = pallet.get().getHeldItem();
        if (!simulate)
            pallet.get().clearContent();
        return item;
    }
}
