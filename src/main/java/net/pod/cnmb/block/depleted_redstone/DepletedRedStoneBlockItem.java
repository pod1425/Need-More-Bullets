package net.pod.cnmb.block.depleted_redstone;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.pod.cnmb.registry.ModDataComponents;

import java.util.List;

public class DepletedRedStoneBlockItem extends BlockItem {
    public DepletedRedStoneBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Power: " + stack.get(ModDataComponents.DEPLETED_REDSTONE_BLOCK_POWER)).withStyle(ChatFormatting.RED));
    }
}
