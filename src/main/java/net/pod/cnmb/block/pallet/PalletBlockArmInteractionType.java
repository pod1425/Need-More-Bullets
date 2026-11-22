package net.pod.cnmb.block.pallet;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.pod.cnmb.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class PalletBlockArmInteractionType extends ArmInteractionPointType {
    @Override
    public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
        return state.is(ModBlocks.PALLET.get());
    }

    @Override
    public @Nullable ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
        return new PalletBlockArmInteraction(this, level, pos, state);
    }
}
