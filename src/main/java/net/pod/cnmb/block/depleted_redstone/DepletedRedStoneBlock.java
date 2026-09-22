package net.pod.cnmb.block.depleted_redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.pod.cnmb.registry.ModDataComponents;

import java.util.ArrayList;
import java.util.List;

public class DepletedRedStoneBlock extends Block {
    public static final IntegerProperty
            POWER = IntegerProperty.create("power", 0, 14),
            OUT_POWER = IntegerProperty.create("out_power", 0, 15);

    public DepletedRedStoneBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(POWER, 14)
                .setValue(OUT_POWER, 14));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        builder.add(OUT_POWER);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int power = state.getValue(POWER);
        if (power == 0) return;
        updateOutPower(state, level, pos, power);
    }

    public boolean extract(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        int power = state.getValue(POWER);

        if (power > 0) {
            power--;
            updateOutPower(state.setValue(POWER, power), level, pos, power);
            return true;
        }
        return false;
    }

    private void updateOutPower(BlockState state, ServerLevel level, BlockPos pos, int power) {
        level.setBlock(pos, state.setValue(OUT_POWER, power == 0 ? 0 : power + (level.random.nextInt(3) - 1)), 3);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos.relative(direction.getOpposite())).is(this) ? 0 : state.getValue(OUT_POWER);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos.relative(direction.getOpposite())).is(this) ? 0 : state.getValue(OUT_POWER);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        ItemStack stack = new ItemStack(asItem());

        stack.set(ModDataComponents.DEPLETED_REDSTONE_BLOCK_POWER.get(), state.getValue(POWER));

        drops.add(stack);
        return drops;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            int power = stack.get(ModDataComponents.DEPLETED_REDSTONE_BLOCK_POWER.get());
            level.setBlock(pos, state.setValue(POWER, power).setValue(OUT_POWER, Math.max(0, power)), 3);
        }
    }
}
