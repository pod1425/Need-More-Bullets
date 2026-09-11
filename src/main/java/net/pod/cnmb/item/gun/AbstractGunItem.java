package net.pod.cnmb.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pod.cnmb.entity.projectile.GenericBulletEntity;
import net.pod.cnmb.item.gun.attachment.GunAttachment;
import net.pod.cnmb.item.gun.attachment.GunAttachmentSlot;
import net.pod.cnmb.item.gun.attachment.GunAttachmentsComponent;
import net.pod.cnmb.registry.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static net.pod.cnmb.registry.ModDataComponents.SELECTED_ATTACHMENT;
import static net.pod.cnmb.registry.ModDataComponents.SHOT_OCCURRED;

public abstract class AbstractGunItem extends Item {
    private final int defaultShootRate;
    private final double defaultBulletDamage;
    private final double defaultBulletSpeed;
    private final double defaultInaccuracy;
    private final boolean defaultIsAutomatic;

    public AbstractGunItem(Properties properties, int shootRate, double bulletDamage,
                           double bulletSpeed, double inaccuracy, boolean isAutomatic, List<GunAttachmentSlot> slots) {
        super(properties
                .component(ModDataComponents.SHOOT_RATE, shootRate)
                .component(ModDataComponents.BULLET_DAMAGE, bulletDamage)
                .component(ModDataComponents.BULLET_SPEED, bulletSpeed)
                .component(ModDataComponents.INACCURACY, inaccuracy)
                .component(ModDataComponents.IS_AUTOMATIC, isAutomatic)
                .component(ModDataComponents.GUN_ATTACHMENTS, GunAttachmentsComponent.create(slots))
                .component(SHOT_OCCURRED, false)
                .component(SELECTED_ATTACHMENT, 0));

        this.defaultShootRate = shootRate;
        this.defaultBulletSpeed = bulletSpeed;
        this.defaultBulletDamage = bulletDamage;
        this.defaultInaccuracy = inaccuracy;
        this.defaultIsAutomatic = isAutomatic;

    }

    public int getBaseShootRate() {
        return defaultShootRate;
    }

    public double getBaseBulletDamage() {
        return defaultBulletDamage;
    }

    public double getBaseInaccuracy() {
        return defaultInaccuracy;
    }

    public double getBaseBulletSpeed() {
        return defaultBulletSpeed;
    }

    public boolean isAutomaticByDefault() {
        return defaultIsAutomatic;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }

    public boolean shoot(Entity entity) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return false;
            }
            player.getCooldowns().addCooldown(this, 20 / defaultShootRate);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        Level l = entity.level();
        if (!l.isClientSide) {
            // creating the object automatically fills in all the necessary data.
            // After initialization, entity is ready to be added to the level
            GenericBulletEntity projectile =
                    new GenericBulletEntity(entity, l, defaultBulletDamage, defaultBulletSpeed, defaultInaccuracy);

            l.addFreshEntity(projectile);

            l.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    ModSounds.Gun_Fire.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {

            // all the right mouse button code goes here

        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            GunPlayerData data = player.getData(ModPlayerAttachments.GUN_DATA);

            if (data.isTriggerPressed()) {
                boolean correctHand = switch (data.getTriggerHand()) {
                    case MAIN_HAND -> player.getMainHandItem() == stack;
                    case OFF_HAND -> player.getOffhandItem() == stack;
                    case BOTH -> player.getMainHandItem() == stack ||
                                    player.getOffhandItem() == stack;
                };
                if (correctHand) {
                    if (defaultIsAutomatic || !data.hasShotOccurred()) {
                        shoot(player);
                    }
                    if (!defaultIsAutomatic) {
                        data.setShotOccurred(true);
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    public boolean tryEquipAttachment(
            ItemStack gunStack,
            ItemStack attachmentStack
    ) {
        System.out.println("Trying to equip attachment");
        GunAttachment attachment =
                ModGunAttachments.ATTACHMENTS.getForItemOrNull(
                        attachmentStack.getItem()
                );

        if (attachment == null)
            return false;

        GunAttachmentsComponent component =
                gunStack.get(ModDataComponents.GUN_ATTACHMENTS.get());

        if (component == null)
            return false;

        for (GunAttachmentSlot slot : component.attachments().keySet()) {
            if (!attachment.isCompatible(slot))
                continue;

            if (!component.get(slot).isEmpty())
                continue;

            gunStack.set(
                    ModDataComponents.GUN_ATTACHMENTS.get(),
                    component.set(
                            slot,
                            attachmentStack.copyWithCount(1)
                    )
            );

            attachmentStack.shrink(1);

            return true;
        }

        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        EnumMap<GunAttachmentSlot, ItemStack> slots =
                stack.get(ModDataComponents.GUN_ATTACHMENTS.get()).attachments();
        int selectedSlot = stack.get(SELECTED_ATTACHMENT.get());
        for (var key : slots.keySet()) {
            Component itemName = slots.get(key).isEmpty()
                    ? Component.translatable("tooltip.cnmb.empty_attachment_slot")
                    : slots.get(key).getHoverName();
            ChatFormatting color = ChatFormatting.GRAY;
            if (selectedSlot == 0) {
                color = Screen.hasControlDown() ? ChatFormatting.RED : ChatFormatting.AQUA;
            }
            tooltip.add(Component.translatable(
                    "tooltip.cnmb.attachment_slot",
                    Component.translatable(key.getTranslationKey()),
                    itemName
            ).withStyle(color));

            selectedSlot--;
        }
        if (!Screen.hasControlDown()) {
            tooltip.add(Component.translatable("tooltip.cnmb.hold_ctrl_remove"));
        } else {
            tooltip.add(Component.translatable("tooltip.cnmb.right_click_to_remove"));
        }
    }

    public void removeAttachment(
            ItemStack gunStack,
            ServerPlayer player,
            int selected
    ) {
        GunAttachmentsComponent component =
                gunStack.get(ModDataComponents.GUN_ATTACHMENTS.get());

        System.out.println(
                "Removing the attachment from item "
                        + gunStack.getDescriptionId()
        );

        if (component == null)
            return;

        System.out.println("selected " + selected);

        List<GunAttachmentSlot> slots =
                new ArrayList<>(component.attachments().keySet());

        if (selected < 0 || selected >= slots.size())
            return;

        GunAttachmentSlot slot = slots.get(selected);

        ItemStack attachment = component.get(slot);

        if (attachment.isEmpty())
            return;

        // Don't overwrite something the player is already carrying.
        if (!player.containerMenu.getCarried().isEmpty())
            return;

        // Put the attachment onto the GUI cursor.
        player.containerMenu.setCarried(attachment.copy());

        gunStack.set(
                ModDataComponents.GUN_ATTACHMENTS.get(),
                component.remove(slot)
        );
    }
}