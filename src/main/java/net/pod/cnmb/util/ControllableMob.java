package net.pod.cnmb.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.pod.cnmb.networking.ModNetworking;
import net.pod.cnmb.networking.payload.ControlInputPayload;
import net.pod.cnmb.networking.payload.SetControllingPayload;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ControllableMob extends Mob implements Controllable {
    public static final Map<UUID, ControllableMob> controls = new HashMap<>();

    public static final TicketType<UUID> TICKET =
            TicketType.create("cnmb:controllable_mob", Comparator.comparing(UUID::toString));

    private ChunkPos chunkPos;
    private int positionSyncTimer;

    private final LongOpenHashSet sent = new LongOpenHashSet();

    public Player controller;
    protected ControlInputPayload input;

    protected ControllableMob(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    public boolean start(ServerPlayer player) {
        if (isControlled() || controls.get(player.getUUID()) != null) return false;

        controller = player;
        controls.put(player.getUUID(), this);

        ModNetworking.sendToPlayer(player, new SetControllingPayload(getId(), true));
        player.connection.send(new ClientboundSetCameraPacket(this));
        player.connection.send(new ClientboundSetChunkCacheRadiusPacket(getViewDistance(player)));

        return true;
    }

    public void stop() {
        if (!(controller instanceof ServerPlayer player)) return;

        ChunkPos playerPos = player.chunkPosition();

        controls.remove(player.getUUID(), this);

        if (player.connection.isAcceptingMessages()) {
            syncControllerPosition(player);

            ChunkPos center = player.chunkPosition();
            int distance = getViewDistance(player);

            player.connection.send(new ClientboundSetChunkCacheCenterPacket(playerPos.x, playerPos.z));
            player.connection.send(new ClientboundSetChunkCacheRadiusPacket(distance));
            player.connection.send(new ClientboundSetCameraPacket(player));
            ModNetworking.sendToPlayer(player, new SetControllingPayload(getId(), false));

            if (!(level() instanceof ServerLevel level)) return;

            LevelLightEngine engine = level.getLightEngine();
            for (int x = -distance; x <= distance; x++) {
                for (int z = -distance; z <= distance; z++) {
                    ChunkPos pos = new ChunkPos(center.x + x, center.z + z);
                    player.connection.send(new ClientboundLevelChunkWithLightPacket(
                            (LevelChunk) level.getChunk(pos.x, pos.z, ChunkStatus.FULL, true), engine, null, null));
                }
            }
        }

        sent.clear();
        controller = null;
        input = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (!(controller instanceof ServerPlayer player)) return;
        if (!player.connection.getConnection().isConnected()) {
            stop();
            return;
        }

        if (++positionSyncTimer >= 3) {
            positionSyncTimer = 0;
            syncControllerPosition(player);
        }

        int distance = getViewDistance(player);

        ChunkPos center = chunkPosition();
        UUID id = getUUID();

        ServerLevel level = player.serverLevel();
        if (!center.equals(chunkPos)) {
            level.getChunkSource().addRegionTicket(TICKET, center, distance, id);

            player.connection.send(new ClientboundSetChunkCacheCenterPacket(center.x, center.z));
            player.connection.send(new ClientboundSetChunkCacheRadiusPacket(distance));

            LongOpenHashSet wanted = new LongOpenHashSet();
            LevelLightEngine engine = level.getLightEngine();
            for (int x = -distance; x <= distance; x++) {
                for (int z = -distance; z <= distance; z++) {
                    ChunkPos pos = new ChunkPos(center.x + x, center.z + z);
                    wanted.add(pos.toLong());

                    if (sent.add(pos.toLong()))
                        player.connection.send(new ClientboundLevelChunkWithLightPacket(
                                (LevelChunk) level.getChunk(pos.x, pos.z, ChunkStatus.FULL, true), engine, null, null));
                }
            }

            sent.removeIf(pos -> !wanted.contains(pos));

            if (chunkPos != null) level.getChunkSource().removeRegionTicket(TICKET, chunkPos, distance, id);
            chunkPos = center;
        }

    }

    private int getViewDistance(ServerPlayer player) {
        return Math.min(player.clientInformation().viewDistance(), player.serverLevel().getServer().getPlayerList().getViewDistance());
    }

    private void syncControllerPosition(ServerPlayer player) {
        player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
    }

    @Override
    public void handleInput(ControlInputPayload input) {
        this.input = input;
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
        if (!level().isClientSide) stop();

        super.remove(reason);
    }
}