package net.pod.cnmb.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.item.gun.attachment.GunAttachmentsComponent;

import java.util.function.UnaryOperator;

@SuppressWarnings("removal")
public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(NeedMoreBulletsMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SHOOT_RATE =
            register("shoot_rate",
                    builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> BULLET_DAMAGE =
            register("bullet_damage",
                    builder -> builder.persistent(Codec.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> BULLET_SPEED =
            register("bullet_speed",
                    builder -> builder.persistent(Codec.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> INACCURACY =
            register("inaccuracy",
                    builder -> builder.persistent(Codec.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_AUTOMATIC =
            register("is_automatic",
                    builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHOT_OCCURRED =
            register("shot_occurred",
                    builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SELECTED_ATTACHMENT =
            register("selected_attachment",
                    builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GunAttachmentsComponent>>
            GUN_ATTACHMENTS = register(
            "gun_attachments",
            builder -> builder.persistent(GunAttachmentsComponent.CODEC)
                    .networkSynchronized(GunAttachmentsComponent.STREAM_CODEC)
    );


    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
