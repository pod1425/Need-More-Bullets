package net.pod.cnmb.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.util.Controllable;
import net.pod.cnmb.util.ControllableMob;

public record ControlInputPayload(float pitch, float yaw,
                                  boolean forward, boolean back, boolean left, boolean right,
                                  boolean up, boolean down) implements CustomPacketPayload {
    public static final Type<ControlInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NeedMoreBulletsMod.MODID,
                    "send_control_input"
            ));
    public static final StreamCodec<ByteBuf, ControlInputPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public ControlInputPayload decode(ByteBuf buf) {
                    float pitch = buf.readFloat(), yaw = buf.readFloat();

                    boolean[] keys = new boolean[6];
                    byte bits = buf.readByte();

                    for (int i = 0; i < 6; i++) {
                        keys[i] = (bits & (1 << i)) != 0;
                    }

                    return new ControlInputPayload(pitch, yaw, keys[0], keys[1], keys[2], keys[3], keys[4], keys[5]);
                }

                @Override
                public void encode(ByteBuf buf, ControlInputPayload payload) {
                    boolean[] keys = {
                            payload.forward(),
                            payload.back(),
                            payload.left(),
                            payload.right(),
                            payload.up(),
                            payload.down()
                    };

                    byte bits = 0;

                    for (int i = 0; i < 6; i++) {
                        if (keys[i]) bits |= 1 << i;
                    }

                    buf.writeFloat(payload.pitch());
                    buf.writeFloat(payload.yaw());
                    buf.writeByte(bits);
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ControlInputPayload payload, IPayloadContext context) {
        Player player = context.player();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        context.enqueueWork(() -> {
            Controllable controllable = ControllableMob.controls.get(serverPlayer.getUUID());
            if (controllable != null) {
                controllable.handleInput(payload);
            }
        });
    }
}