package dev.shadowsoffire.apotheosis.spawn.modifiers;

import com.google.gson.JsonElement;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpawnerStats {

    public static final Map<String, SpawnerStat<?>> REGISTRY = new HashMap<>();

    public static final SpawnerStat<Integer> MIN_DELAY = register(new IntStat("min_delay", s -> s.spawner.minSpawnDelay, (s, v) -> s.spawner.minSpawnDelay = v));

    public static final SpawnerStat<Integer> MAX_DELAY = register(new IntStat("max_delay", s -> s.spawner.maxSpawnDelay, (s, v) -> s.spawner.maxSpawnDelay = v));

    public static final SpawnerStat<Integer> SPAWN_COUNT = register(new IntStat("spawn_count", s -> s.spawner.spawnCount, (s, v) -> s.spawner.spawnCount = v));

    public static final SpawnerStat<Integer> MAX_NEARBY_ENTITIES = register(new IntStat("max_nearby_entities", s -> s.spawner.maxNearbyEntities, (s, v) -> s.spawner.maxNearbyEntities = v));

    public static final SpawnerStat<Integer> REQ_PLAYER_RANGE = register(new IntStat("req_player_range", s -> s.spawner.requiredPlayerRange, (s, v) -> s.spawner.requiredPlayerRange = v));

    public static final SpawnerStat<Integer> SPAWN_RANGE = register(new IntStat("spawn_range", s -> s.spawner.spawnRange, (s, v) -> s.spawner.spawnRange = v));

    public static final SpawnerStat<Boolean> IGNORE_PLAYERS = register(new BoolStat("ignore_players", s -> ((IBaseSpawner) s).getIgnorePlayers(), (s, v) -> ((IBaseSpawner) s).setIgnoresPlayers(v)));

    public static final SpawnerStat<Boolean> IGNORE_CONDITIONS = register(new BoolStat("ignore_conditions", s -> ((IBaseSpawner) s).getIgnoresConditions(), (s, v) -> ((IBaseSpawner) s).setIgnoresConditions(v)));

    public static final SpawnerStat<Boolean> REDSTONE_CONTROL = register(new BoolStat("redstone_control", s -> ((IBaseSpawner) s).getRedstoneControl(), (s, v) -> ((IBaseSpawner) s).setRedstoneControl(v)));

    public static final SpawnerStat<Boolean> IGNORE_LIGHT = register(new BoolStat("ignore_light", s -> ((IBaseSpawner) s).getIgnoreLight(), (s, v) -> ((IBaseSpawner) s).setIgnoreLight(v)));

    public static final SpawnerStat<Boolean> NO_AI = register(new BoolStat("no_ai", s -> ((IBaseSpawner) s).getNoAi(), (s, v) -> ((IBaseSpawner) s).setNoAi(v)));

    public static final SpawnerStat<Boolean> SILENT = register(new BoolStat("silent", s -> ((IBaseSpawner) s).getSilent(), (s, v) -> ((IBaseSpawner) s).setSilent(v)));

    private static <T extends SpawnerStat<?>> T register(T t) {
        REGISTRY.put(t.getId(), t);
        return t;
    }

    private static abstract class Base<T> implements SpawnerStat<T> {

        protected final String id;
        protected final Function<SpawnerBlockEntity, T> getter;
        protected final BiConsumer<SpawnerBlockEntity, T> setter;

        private Base(String id, Function<SpawnerBlockEntity, T> getter, BiConsumer<SpawnerBlockEntity, T> setter) {
            this.id = id;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public String getId() {
            return this.id;
        }

    }

    private static class BoolStat extends Base<Boolean> {

        private BoolStat(String id, Function<SpawnerBlockEntity, Boolean> getter, BiConsumer<SpawnerBlockEntity, Boolean> setter) {
            super(id, getter, setter);
        }

        @Override
        public Boolean parseValue(JsonElement value) {
            return value == null ? false : value.getAsBoolean();
        }

        @Override
        public boolean apply(Boolean value, Boolean min, Boolean max, SpawnerBlockEntity spawner) {
            boolean old = this.getter.apply(spawner);
            this.setter.accept(spawner, value);
            return old != this.getter.apply(spawner);
        }

        @Override
        public Class<Boolean> getTypeClass() {
            return Boolean.class;
        }
    }

    private static class IntStat extends Base<Integer> {

        private IntStat(String id, Function<SpawnerBlockEntity, Integer> getter, BiConsumer<SpawnerBlockEntity, Integer> setter) {
            super(id, getter, setter);
        }

        @Override
        public Integer parseValue(JsonElement value) {
            return value == null ? 0 : value.getAsInt();
        }

        @Override
        public boolean apply(Integer value, Integer min, Integer max, SpawnerBlockEntity spawner) {
            int old = this.getter.apply(spawner);
            this.setter.accept(spawner, Mth.clamp(old + value, min, max));
            return old != this.getter.apply(spawner);
        }

        @Override
        public Class<Integer> getTypeClass() {
            return Integer.class;
        }
    }

}
