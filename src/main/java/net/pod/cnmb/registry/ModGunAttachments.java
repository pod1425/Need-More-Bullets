package net.pod.cnmb.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.pod.cnmb.item.gun.AbstractGunItem;
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.registry.custom.SimpleGunAttachmentHolder;
import net.pod.cnmb.registry.custom.SimpleGunAttachmentRegistry;

import java.util.List;


public class ModGunAttachments {
    public static final SimpleGunAttachmentRegistry ATTACHMENTS =
            new SimpleGunAttachmentRegistry();

    public static final SimpleGunAttachmentHolder SPYGLASS_SCOPE =
            ATTACHMENTS.register(
                    "spyglass_scope",
                    Items.SPYGLASS,
                    () -> GunAttachment.createScope()
                            .onAim(args -> {
                                if (args.getShooter() instanceof Player player) {
                                    player.displayClientMessage(
                                            Component.literal("Aiming!"),
                                            true
                                    );
                                }
                            })
            );

    public static final SimpleGunAttachmentHolder REDSTONE_POWERUP =
            ATTACHMENTS.register(
                    "redstone_powerup",
                    Items.REDSTONE,
                    () -> GunAttachment.createBarrel()
                            .onBlockHit(a -> a.getLevel().destroyBlock(
                                    a.getPos(), true, a.getShooter()))
                            .onGunEquip(
                                a -> a.getGunItem().set(ModDataComponents.BULLET_DAMAGE.get(), 20.0))
            );


}
