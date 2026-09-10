package net.pod.cnmb.util;

import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.pod.cnmb.networking.payload.ControlInputPayload;
import net.pod.cnmb.networking.ModNetworking;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public abstract class ControllableMob extends Mob implements Controllable {
    public static final Map<UUID, ControllableMob> controls = new HashMap<>();

    public static final TicketType<ChunkPos> CHUNK_TICKET =
            TicketType.create("cnmb_controllable_mob_chunk_loader", Comparator.comparingLong(ChunkPos::toLong));

    public ChunkPos chunkPos;
    private Set<ChunkPos> loadedChunks = new HashSet<>();

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

        ServerPlayer player = (ServerPlayer) controller;

        controls.remove(controller.getUUID());
        player.connection.send(new ClientboundSetCameraPacket(controller));
        ModNetworking.sendControlling(player, getId(), false);

        if (level() instanceof ServerLevel level) {
            loadedChunks.forEach(p -> player.connection.send(new ClientboundForgetLevelChunkPacket(p)));
            loadedChunks.clear();

            ChunkPos pos = controller.chunkPosition();
            player.connection.send(new ClientboundSetChunkCacheCenterPacket(pos.x, pos.z));

            int radius = player.requestedViewDistance();
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    player.connection.send(
                            new ClientboundLevelChunkWithLightPacket(
                                    (LevelChunk) level.getChunk(pos.x + dx, pos.z + dz, ChunkStatus.FULL),
                                    level.getLightEngine(), null, null)
                    );
                }
            }

            chunkPos = null;
        }

        controller = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (level() instanceof ServerLevel level && controller instanceof ServerPlayer player && isControlled()) {
            ChunkPos pos = chunkPosition();
            if (!pos.equals(chunkPos)) {
                player.connection.send(new ClientboundSetChunkCacheCenterPacket(pos.x, pos.z));

                int radius = player.requestedViewDistance();
                Set<ChunkPos> needed = new HashSet<>();
                for (int dx = -radius - 2; dx <= radius + 2; dx++) {
                    for (int dz = -radius - 2; dz <= radius + 2; dz++) {
                        needed.add(new ChunkPos(pos.x + dx, pos.z + dz));
                    }
                }

                loadedChunks.removeIf(p -> {
                    if (!needed.contains(p)) {
                        player.connection.send(new ClientboundForgetLevelChunkPacket(p));
                        return true;
                    }
                    return false;
                });

                needed.forEach(p -> {
                    if (!loadedChunks.contains(p)) {
                        level.getChunkSource().getChunkFuture(p.x, p.z, ChunkStatus.FULL, true)
                                .thenAcceptAsync(res -> res.ifSuccess(access -> {
                                    if (access instanceof LevelChunk chunk && player.connection.isAcceptingMessages() && controller == player) {
                                        player.connection.send(new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null));
                                    }
                                }), level.getServer());
                    }
                });

                level.getChunkSource().addRegionTicket(CHUNK_TICKET, pos, radius, pos);
                if (chunkPos != null) {
                    level.getChunkSource().removeRegionTicket(CHUNK_TICKET, chunkPos, radius, chunkPos);
                }

                chunkPos = pos;
                loadedChunks = needed;
            }
        }
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isControlled() && super.removeWhenFarAway(distanceToClosestPlayer);
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
