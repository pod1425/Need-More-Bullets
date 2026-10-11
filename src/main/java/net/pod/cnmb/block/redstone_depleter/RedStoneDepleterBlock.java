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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pod.cnmb.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class RedStoneDepleterBlock extends KineticBlock implements IBE<RedStoneDepleterBlockEntity>, IWrenchable, ICogWheel {
    public static final BooleanProperty IS_CHARGING_MODE = BooleanProperty.create("is_charging_mode");

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public RedStoneDepleterBlock(Properties properties) {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(IS_CHARGING_MODE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_CHARGING_MODE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(IS_CHARGING_MODE) ? SHAPE : Shapes.block();
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
        ((RedStoneDepleterBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos())).toggleMode();
        return InteractionResult.SUCCESS;
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
