package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.attributeslib.components.BooleanComponent;
import dev.shadowsoffire.attributeslib.components.SyncedBooleanComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ZenithComponents implements EntityComponentInitializer {
    public static final ComponentKey<SyncedBooleanComponent> MOVABLE = ComponentRegistry.getOrCreate(Apotheosis.loc("movable"), SyncedBooleanComponent.class);
    public static final ComponentKey<AffixDataComponent> AFFIX_DATA = ComponentRegistry.getOrCreate(Apotheosis.loc("affix_data"), AffixDataComponent.class);
    public static final ComponentKey<AffixCooldownComponent> AFFIX_COOLDOWN = ComponentRegistry.getOrCreate(Apotheosis.loc("affix_cooldown"), AffixCooldownComponent.class);
    public static final ComponentKey<BossComponent> BOSS_DATA = ComponentRegistry.getOrCreate(Apotheosis.loc("boss_data"), BossComponent.class);
    public static final ComponentKey<BooleanComponent> BURNS = ComponentRegistry.getOrCreate(Apotheosis.loc("burns_in_sun"), BooleanComponent.class);
    public static final ComponentKey<BooleanComponent> GENERATED_ARROW = ComponentRegistry.getOrCreate(Apotheosis.loc("generated"), BooleanComponent.class);

    public static final ComponentKey<BooleanComponent> NO_PINATA = ComponentRegistry.getOrCreate(Apotheosis.loc("no_pinata"), BooleanComponent.class);
    public static final ComponentKey<StringComponent> RADIAL_STATE = ComponentRegistry.getOrCreate(Apotheosis.loc("radial_state"), StringComponent.class);
    public static final ComponentKey<IntComponent> REFORGING_SEED = ComponentRegistry.getOrCreate(Apotheosis.loc("reforging_seed"), IntComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.registerFor(LivingEntity.class, MOVABLE, entity -> new SyncedBooleanComponent(Apotheosis.loc("movable").toString()));

        registry.registerFor(LivingEntity.class, AFFIX_COOLDOWN, entity -> new AffixCooldownComponent());
        registry.registerFor(Entity.class, AFFIX_DATA, entity -> new AffixDataComponent());

        registry.registerFor(LivingEntity.class, BOSS_DATA, entity -> new BossComponent());
        registry.registerFor(LivingEntity.class, BURNS, entity -> new BooleanComponent(Apotheosis.loc("burns_in_sun").toString()));
        registry.registerFor(AbstractArrow.class, GENERATED_ARROW, arrow -> new BooleanComponent(Apotheosis.loc("generated").toString()));
        registry.registerFor(LivingEntity.class, NO_PINATA, entity -> new BooleanComponent(Apotheosis.loc("no_pinata").toString()));

        registry.registerForPlayers(RADIAL_STATE, player -> new StringComponent(Apotheosis.loc("radial_state").toString()), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(REFORGING_SEED, player -> new IntComponent(Apotheosis.loc("reforging_seed").toString()), RespawnCopyStrategy.ALWAYS_COPY);

    }

}
