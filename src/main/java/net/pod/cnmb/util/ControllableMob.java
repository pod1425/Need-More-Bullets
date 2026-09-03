package net.pod.cnmb.util;

import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.pod.cnmb.networking.ControlInputPayload;
import net.pod.cnmb.networking.ModNetworking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ControllableMob extends Mob implements Controllable {
    public static final Map<UUID, ControllableMob> controls = new HashMap<>();

    public Player controller;
    protected ControlInputPayload input;

    protected ControllableMob(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    protected boolean start(ServerPlayer player) {
        if (isControlled() || controls.containsKey(player.getUUID())) {
            return false;
        }

        controller = player;

        ModNetworking.sendControlling(player, getId(), true);
        player.connection.send(new ClientboundSetCameraPacket(this));
        controls.put(player.getUUID(), this);

        return true;
    }

    public void stop() {
        if (controller == null) {
            return;
        }

        ServerPlayer player = (ServerPlayer)controller;

        controls.remove(controller.getUUID());
        player.connection.send(new ClientboundSetCameraPacket(controller));
        ModNetworking.sendControlling(player, getId(), false);

        controller = null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getMainHandItem().isEmpty() && player.isShiftKeyDown() && player instanceof ServerPlayer serverPlayer) {
            return start(serverPlayer) ? InteractionResult.SUCCESS_NO_ITEM_USED : InteractionResult.FAIL;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void handleInput(ControlInputPayload input) {
        this.input = input;

        setYRot(yBodyRot = yHeadRot += input.yaw());
        setXRot(xRotO = Mth.clamp(getXRot() + input.pitch(), -90, 90));
    }

    public boolean isControlled() {
        return controller != null;
    }

    @Override
    public boolean isNoAi() {
        return isControlled();
    }

    protected double getControlSpeed() {
        return getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) {
            stop();
        }

        super.remove(reason);
    }
}
