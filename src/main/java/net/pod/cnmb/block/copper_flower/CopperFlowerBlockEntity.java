package net.pod.cnmb.block.copper_flower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pod.cnmb.entity.beedrone.BeeDrone;
import net.pod.cnmb.registry.ModBlockEntities;

import java.util.List;

public class CopperFlowerBlockEntity extends BlockEntity {
    public static final int CAPACITY = 100000;
    public static final int MAX_RECEIVE = 512;
    public static final int TRANSFER_PER_TICK = 128;
    public static final double CHARGE_RADIUS = 3.0D;

    private final EnergyStorage storage = new EnergyStorage(CAPACITY, MAX_RECEIVE, TRANSFER_PER_TICK, CAPACITY);
    private final IEnergyStorage istorage = new IEnergyStorage() {
        @Override public int receiveEnergy(int toReceive, boolean simulate) {return storage.receiveEnergy(toReceive, simulate);}
        @Override public int extractEnergy(int toExtract, boolean simulate) {return 0;}
        @Override public int getEnergyStored() {return storage.getEnergyStored();}
        @Override public int getMaxEnergyStored() {return storage.getMaxEnergyStored();}
        @Override public boolean canExtract() {return false;}
        @Override public boolean canReceive() {return true;}
    };

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_FLOWER.get(),
                (be, side) -> side == Direction.DOWN ? be.istorage : null);
    }

    public CopperFlowerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COPPER_FLOWER.get(), pos, blockState);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        AABB chargeArea = new AABB(getBlockPos()).inflate(CHARGE_RADIUS);
        List<BeeDrone> drones = level.getEntitiesOfClass(BeeDrone.class, chargeArea);

        for (BeeDrone drone : drones) {
            IEnergyStorage droneEnergy = drone.getCapability(Capabilities.EnergyStorage.ENTITY, null);
            if (droneEnergy == null || !droneEnergy.canReceive()) continue;

            int available = storage.extractEnergy(TRANSFER_PER_TICK, true);
            if (available <= 0) continue;

            int received = droneEnergy.receiveEnergy(available, false);
            if (received > 0) storage.extractEnergy(received, false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.put("Energy", storage.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains("Energy")) storage.deserializeNBT(provider, tag.get("Energy"));
    }
}