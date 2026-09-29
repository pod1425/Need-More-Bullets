package net.pod.cnmb.block.redstone_depleter;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pod.cnmb.registry.ModBlockEntities;
import net.pod.cnmb.registry.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedStoneDepleterBlock extends KineticBlock implements IBE<RedStoneDepleterBlockEntity>, IWrenchable, ICogWheel {
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public RedStoneDepleterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public Class<RedStoneDepleterBlockEntity> getBlockEntityClass() {
        return RedStoneDepleterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedStoneDepleterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.REDSTONE_DEPLETER.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.REDSTONE_DEPLETER.get().create(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand == InteractionHand.MAIN_HAND && hitResult.getDirection() == Direction.UP) {
            if (level.getBlockEntity(pos) instanceof RedStoneDepleterBlockEntity be) {
                IEnergyStorage storage = stack.copyWithCount(1).getCapability(Capabilities.EnergyStorage.ITEM);
                if (stack.isEmpty() || (storage != null && storage.canReceive())) return be.useItemOn(player);
            }
        }

        return ItemInteractionResult.FAIL;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(ModBlocks.REDSTONE_DEPLETER.toStack());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()))
            if (level.getBlockEntity(pos) instanceof RedStoneDepleterBlockEntity be && be.getItem() instanceof ItemStack item && !item.isEmpty())
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }
}
