package net.pod.cnmb.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pod.cnmb.entity.beedrone.BeeDrone;
import net.pod.cnmb.registry.ModDataComponents;

import java.util.UUID;

public class BeeDroneController extends Item {
    public BeeDroneController(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level.isClientSide) return InteractionResultHolder.success(stack);

        UUID id = stack.get(ModDataComponents.BEE_DRONE_CONTROLLER_ID.get());
        if (id == null) return InteractionResultHolder.pass(stack);

        if (!(((ServerLevel) level).getEntity(id) instanceof BeeDrone bee)) return InteractionResultHolder.pass(stack);
        bee.start((ServerPlayer) player);

        return InteractionResultHolder.success(stack);
    }
}
