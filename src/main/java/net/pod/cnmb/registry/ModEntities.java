package net.pod.cnmb.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pod.cnmb.NeedMoreBulletsMod;
import net.pod.cnmb.entity.lead_golem.LeadGolem;
import net.pod.cnmb.entity.projectile.GenericBulletEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, NeedMoreBulletsMod.MODID);

    public static final Supplier<EntityType<GenericBulletEntity>> GENERIC_BULLET =
            ENTITY_TYPES.register("generic_bullet", () -> EntityType.Builder
                    .<GenericBulletEntity>of(GenericBulletEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("generic_bullet"));

    public static final Supplier<EntityType<LeadGolem>> LEAD_GOLEM =
            ENTITY_TYPES.register("lead_golem", () -> EntityType.Builder.of(LeadGolem::new, MobCategory.CREATURE)
                    .sized(0.75f, 1.5f).build("lead_golem"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}