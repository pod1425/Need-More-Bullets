package net.pod.cnmb.block.slip_roller;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.pod.cnmb.block.slip_roller.client.SlipRollerBlockRenderer;
import net.pod.cnmb.block.slip_roller.client.SlipRollerItemRenderer;
import net.pod.cnmb.registry.ModBlockEntities;
import net.pod.cnmb.registry.ModItems;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class SlipRollerBlockEntity extends KineticBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private record Craft(Item input, Item output, Integer ticks) {
        private static final List<Craft> ALL = List.of(
                new Craft(AllItems.COPPER_SHEET.get(), AllBlocks.FLUID_PIPE.asItem(), 40)
        );

        public static @Nullable Craft find(Item input) {
            for (Craft craft : ALL)
                if (craft.input.equals(input)) return craft;
            return null;
        }
    }

    private Craft craft = null;
    private int timer;
    private float progress;

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SLIP_ROLLER.get(), SlipRollerBlockRenderer::new);
    }
    public static void registerRenderer(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new IClientItemExtensions() {
                    private final SlipRollerItemRenderer renderer = new SlipRollerItemRenderer();

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return renderer;
                    }
                },
                ModItems.SLIP_ROLLER.get()
        );
    }

    private static class ItemHandler implements IItemHandler {
        private final SlipRollerBlockEntity be;

        public ItemHandler(SlipRollerBlockEntity be) {
            this.be = be;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return be.timer == 0 && be.craft != null ? new ItemStack(be.craft.output) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int i, ItemStack stack, boolean b) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int i, int i1, boolean b) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int i) {
            return 1;
        }

        @Override
        public boolean isItemValid(int i, ItemStack item) {
            return Craft.find(item.getItem()) != null;
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SLIP_ROLLER.get(),
                (be, side) -> side == Direction.UP ? new ItemHandler(be) {
                    @Override
                    public ItemStack insertItem(int i, ItemStack item, boolean simulate) {
                        if (be.craft != null || !isItemValid(i, item)) return item;

                        if (!simulate) {
                            be.craft = Craft.find(item.getItem());
                            be.timer = be.craft.ticks;
                            be.setChanged();
                        }

                        ItemStack rest = item.copy();
                        rest.shrink(1);
                        return rest.getCount() == 0 ? ItemStack.EMPTY : rest;
                    }
                } : side == be.getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING) ?
                        new ItemHandler(be) {
                            @Override
                            public ItemStack extractItem(int i, int i1, boolean simulate) {
                                if (be.timer == 0 && be.craft != null) {
                                    ItemStack item = new ItemStack(be.craft.output);
                                    if (!simulate) be.craft = null;
                                    return item;
                                }

                                return ItemStack.EMPTY;
                            }
                        } : null);
    }

    public SlipRollerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SLIP_ROLLER.get(), pos, blockState);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || getSpeed() == 0) return;

        if (timer > 0) {
            progress += Math.abs(getSpeed()) / 64;
            while (progress >= 1f) {
                timer--;
                progress--;
                if (timer == 0) {
                    progress = 0f;
                    break;
                }
            }
            return;
        }

        if (pushForward()) {
            BlockPos above = worldPosition.above();
            AABB box = new AABB(
                    above.getX() + 0.2, above.getY(), above.getZ() + 0.2,
                    above.getX() + 0.8, above.getY() + 0.1, above.getZ() + 0.8);
            for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, box)) {
                ItemStack stack = entity.getItem();

                craft = Craft.find(stack.getItem());
                if (craft == null) continue;

                ItemStack rest = stack.copy();
                rest.shrink(1);
                if (rest.isEmpty()) entity.discard();
                else entity.setItem(rest);

                timer = craft.ticks;
                return;
            }
        }
    }

    private boolean pushForward() {
        if (craft == null) return true;

        Direction front = getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        BlockPos frontPos = worldPosition.relative(front);

        if (level.getBlockState(frontPos).getCollisionShape(level, frontPos).isEmpty()) {
            spawnForward(front);
            return true;
        } else {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, frontPos, front.getOpposite());
            if (handler != null) {
                if (ItemHandlerHelper.insertItem(handler, new ItemStack(craft.output), false).isEmpty()) {
                    craft = null;
                    return true;
                }
            }
        }

        return false;
    }

    private void spawnForward(Direction front) {
        ItemEntity entity = new ItemEntity(level,
                worldPosition.getX() + 0.5 + front.getStepX() * 0.7f,
                worldPosition.getY() + 0.5 - 0.15625f,
                worldPosition.getZ() + 0.5 + front.getStepZ() * 0.7f, new ItemStack(craft.output));
        double speed = level.random.nextDouble() * 0.05 + 0.1;
        double spread = 0.02;
        entity.setDeltaMovement(
                level.random.triangle(front.getStepX() * speed, spread),
                level.random.triangle(0, spread),
                level.random.triangle(front.getStepZ() * speed, spread));
        level.addFreshEntity(entity);
        craft = null;
    }

    @Override
    public float calculateStressApplied() {
        return 4;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (level == null || level.isClientSide || craft == null) return;

        Containers.dropItemStack(level,
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                new ItemStack(craft.input()));
        craft = null;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Timer", timer);
        if (craft != null) compound.putString("Craft", BuiltInRegistries.ITEM.getKey(craft.input()).toString());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        timer = compound.getInt("Timer");
        craft = compound.contains("Craft") ? Craft.find(BuiltInRegistries.ITEM.get(ResourceLocation.parse(compound.getString("Craft")))) : null;
    }
}