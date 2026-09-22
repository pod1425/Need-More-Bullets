package net.pod.cnmb.block.redstone_depleter;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.pod.cnmb.block.depleted_redstone.DepletedRedStoneBlock;
import net.pod.cnmb.registry.ModBlockEntities;
import net.pod.cnmb.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedStoneDepleterBlockEntity extends BlockEntity implements IHaveGoggleInformation {
    private static final int
            CAPACITY = 100000,
            ADDED_ENERGY = 8000,
            MAX_EXTRACT = 100,
            TIMER = 300;

    private final EnergyStorage energyStorage = new EnergyStorage(CAPACITY, ADDED_ENERGY, MAX_EXTRACT);
    private int timer = TIMER;

    public RedStoneDepleterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REDSTONE_DEPLETER.get(), pos, blockState);
    }

    public void tick() {
        if (level == null || !(level instanceof ServerLevel lvl)) return;

        if (level.getGameTime() % 5 == 0) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);

        if (timer > 0) {
            timer--;
        }

        if (timer == 0) {
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
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
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

        tooltip.add(Component.literal("    §eEnergy: §f" + formatFE(energyStorage.getEnergyStored()) + " / " + formatFE(energyStorage.getMaxEnergyStored())));

        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("EnergyStorage", energyStorage.serializeNBT(provider));
        tag.putInt("Timer", timer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("EnergyStorage")) energyStorage.deserializeNBT(provider, tag.get("EnergyStorage"));
        if (tag.contains("Timer")) timer = tag.getInt("Timer");
    }
}
