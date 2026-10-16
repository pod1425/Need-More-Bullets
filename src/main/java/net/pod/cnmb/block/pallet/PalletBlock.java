package net.pod.cnmb.block.pallet;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmItem;
import com.simibubi.create.content.logistics.depot.SharedDepotBlockMethods;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pod.cnmb.registry.ModBlockEntities;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Locale;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PalletBlock extends Block implements IBE<PalletBlockEntity>, IWrenchable, ProperWaterloggedBlock {

    public static final EnumProperty<MultiblockPlacement> MULTIBLOCK_PLACEMENT = EnumProperty.create("multiblock_placement", MultiblockPlacement.class);

    public PalletBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                defaultBlockState()
                        .setValue(WATERLOGGED, false)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(MULTIBLOCK_PLACEMENT, MultiblockPlacement.NONE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(MULTIBLOCK_PLACEMENT)) {
            case NW -> Block.box(0, 0, 0, 32, 8, 32);
            case NE -> Block.box(-16, 0, 0, 16, 8, 32);
            case SW -> Block.box(0, 0, -16, 32, 8, 16);
            case SE -> Block.box(-16, 0, -16, 16, 8, 16);
            default -> getDefaultShape();
        };
    }
    public static VoxelShape getDefaultShape() {
        return Block.box(0, 0, 0, 16, 8, 16);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(WATERLOGGED).add(FACING).add(MULTIBLOCK_PLACEMENT));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            checkMultiblock(level, pos);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        updateWater(level, state, pos);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof ArmItem)
            return ItemInteractionResult.SUCCESS;
        getBlockEntityOptional(level, pos).ifPresent(blockEntity -> {
            var heldItem = blockEntity.getHeldItem();
            if (stack.isEmpty()){ // empty hand -> only take
                if (!heldItem.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(heldItem);
                    blockEntity.clearContent();
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + level.getRandom().nextFloat());
                }
            } else { // something in hand
                if (heldItem.isEmpty()) { // if pallet empty only insert
                    blockEntity.setHeldItem(stack);
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    AllSoundEvents.DEPOT_SLIDE.playOnServer(level, pos);
                } else { // otherwise switch items and play 2 sounds
                    blockEntity.setHeldItem(stack);
                    player.setItemInHand(hand, heldItem);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + level.getRandom().nextFloat());
                    AllSoundEvents.DEPOT_SLIDE.playOnServer(level, pos);
                }
            }
        });
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        super.updateEntityAfterFallOn(level, entity);
        // I don't see point to implement it by my own
        SharedDepotBlockMethods.onLanded(level, entity);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
        if (!level.isClientSide && !newState.is(this)) {
            removeMultiblock(level, pos, state.getValue(MULTIBLOCK_PLACEMENT));
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return getBlockEntityOptional(level, pos)
                .map(be -> {
                    float f = be.getHeldItem().getCount();
                    int max = be.getHeldItem().getMaxStackSize();
                    f = f / (max == 0 ? 64 : max);
                    return Mth.clamp(Mth.floor(f * 14.0F) + (f > 0 ? 1 : 0), 0, 15);
                })
                .orElse(0);
    }

    @Override
    public Class<PalletBlockEntity> getBlockEntityClass() {
        return PalletBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PalletBlockEntity> getBlockEntityType() {
        return ModBlockEntities.PALLET.get();
    }


    private void checkMultiblock(Level level, BlockPos pos) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                var p1 = pos.offset(dx, 0, dz); // NW origin
                var p2 = p1.east(); // NE
                var p3 = p1.south(); // SW
                var p4 = p1.east().south(); // SE

                if (suitableForMultiblock(level, p1)
                        && suitableForMultiblock(level, p2)
                        && suitableForMultiblock(level, p3)
                        && suitableForMultiblock(level, p4)
                        && hasSameFacing(level, p1, p2, p3, p4)) {
                    setMultiblock(level, p1, MultiblockPlacement.NW);
                    setMultiblock(level, p2, MultiblockPlacement.NE);
                    setMultiblock(level, p3, MultiblockPlacement.SW);
                    setMultiblock(level, p4, MultiblockPlacement.SE);
                }
            }
        }
    }

    private boolean hasSameFacing(Level level, BlockPos... positions) {
        Direction facing = level.getBlockState(positions[0]).getValue(FACING);
        for (BlockPos pos : positions) {
            if (level.getBlockState(pos).getValue(FACING) != facing) {
                return false;
            }
        }
        return true;
    }

    private boolean suitableForMultiblock(Level level, BlockPos pos) {
        var bs = level.getBlockState(pos);
        return bs.is(this) && bs.getValue(MULTIBLOCK_PLACEMENT).equals(MultiblockPlacement.NONE);
    }

    private void setMultiblock(Level level, BlockPos pos, MultiblockPlacement placement) {
        var bs = level.getBlockState(pos);

        if (bs.is(this) && !bs.getValue(MULTIBLOCK_PLACEMENT).equals(placement)) {
            level.setBlock(pos, bs.setValue(MULTIBLOCK_PLACEMENT, placement), Block.UPDATE_CLIENTS);
        }
    }

    private void removeMultiblock(Level level, BlockPos pos, MultiblockPlacement placement) {
        if (placement.equals(MultiblockPlacement.NONE))
            return;

        var neighbors = MultiblockPlacement.neighbors(placement, pos);
        for (BlockPos neighborPos : neighbors) {
            var neighbor = level.getBlockState(neighborPos).setValue(MULTIBLOCK_PLACEMENT, MultiblockPlacement.NONE);
            level.setBlock(neighborPos, neighbor, Block.UPDATE_CLIENTS);
        }
    }

    public enum MultiblockPlacement implements StringRepresentable {
        NW, NE, SW, SE, NONE;

        public static BlockPos[] neighbors(MultiblockPlacement d, BlockPos pos) {
            return switch (d) {
                case NW -> new BlockPos[]{pos.offset(1, 0, 0), pos.offset(0, 0, 1), pos.offset(1, 0, 1)};
                case NE -> new BlockPos[]{pos.offset(-1, 0, 0), pos.offset(0, 0, 1), pos.offset(-1, 0, 1)};
                case SW -> new BlockPos[]{pos.offset(1, 0, 0), pos.offset(0, 0, -1), pos.offset(1, 0, -1)};
                case SE -> new BlockPos[]{pos.offset(-1, 0, 0), pos.offset(0, 0, -1), pos.offset(-1, 0, -1)};
                case NONE -> new BlockPos[0];
            };
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
