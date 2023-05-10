package safro.zenith.spawn.modifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.gson.JsonElement;

import net.minecraft.util.Mth;
import safro.zenith.util.IBaseSpawner;

public class SpawnerStats {

	public static final Map<String, SpawnerStat<?>> REGISTRY = new HashMap<>();

	public static final SpawnerStat<Integer> MIN_DELAY = register(new IntStat("min_delay", s -> s.getSpawner().minSpawnDelay, (s, v) -> s.getSpawner().minSpawnDelay = v));

	public static final SpawnerStat<Integer> MAX_DELAY = register(new IntStat("max_delay", s -> s.getSpawner().maxSpawnDelay, (s, v) -> s.getSpawner().maxSpawnDelay = v));

	public static final SpawnerStat<Integer> SPAWN_COUNT = register(new IntStat("spawn_count", s -> s.getSpawner().spawnCount, (s, v) -> s.getSpawner().spawnCount = v));

	public static final SpawnerStat<Integer> MAX_NEARBY_ENTITIES = register(new IntStat("max_nearby_entities", s -> s.getSpawner().maxNearbyEntities, (s, v) -> s.getSpawner().maxNearbyEntities = v));

	public static final SpawnerStat<Integer> REQ_PLAYER_RANGE = register(new IntStat("req_player_range", s -> s.getSpawner().requiredPlayerRange, (s, v) -> s.getSpawner().requiredPlayerRange = v));

	public static final SpawnerStat<Integer> SPAWN_RANGE = register(new IntStat("spawn_range", s -> s.getSpawner().spawnRange, (s, v) -> s.getSpawner().spawnRange = v));

	public static final SpawnerStat<Boolean> IGNORE_PLAYERS = register(new BoolStat("ignore_players", IBaseSpawner::getIgnoresPlayers, IBaseSpawner::setIgnoresPlayers));

	public static final SpawnerStat<Boolean> IGNORE_CONDITIONS = register(new BoolStat("ignore_conditions", IBaseSpawner::getIgnoresConditions, IBaseSpawner::setIgnoresConditions));

	public static final SpawnerStat<Boolean> REDSTONE_CONTROL = register(new BoolStat("redstone_control", IBaseSpawner::getRedstoneControl,IBaseSpawner::setRedstoneControl));

	public static final SpawnerStat<Boolean> IGNORE_LIGHT = register(new BoolStat("ignore_light", IBaseSpawner::getIgnoreLight, IBaseSpawner::setIgnoreLight));

	public static final SpawnerStat<Boolean> NO_AI = register(new BoolStat("no_ai", IBaseSpawner::getNoAi, IBaseSpawner::setNoAi));

	public static final SpawnerStat<Boolean> SILENT = register(new BoolStat("silent", IBaseSpawner::getSilent, IBaseSpawner::setSilent));

	private static <T extends SpawnerStat<?>> T register(T t) {
		REGISTRY.put(t.getId(), t);
		return t;
	}

	private static abstract class Base<T> implements SpawnerStat<T> {

		protected final String id;
		protected final Function<IBaseSpawner, T> getter;
		protected final BiConsumer<IBaseSpawner, T> setter;

		private Base(String id, Function<IBaseSpawner, T> getter, BiConsumer<IBaseSpawner, T> setter) {
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

		private BoolStat(String id, Function<IBaseSpawner, Boolean> getter, BiConsumer<IBaseSpawner, Boolean> setter) {
			super(id, getter, setter);
		}

		@Override
		public Boolean parseValue(JsonElement value) {
			return value == null ? false : value.getAsBoolean();
		}

		@Override
		public boolean apply(Boolean value, Boolean min, Boolean max, IBaseSpawner spawner) {
			boolean old = getter.apply(spawner);
			setter.accept(spawner, value);
			return old != getter.apply(spawner);
		}


		@Override
		public Class<Boolean> getTypeClass() {
			return Boolean.class;
		}
	}

	private static class IntStat extends Base<Integer> {

		private IntStat(String id, Function<IBaseSpawner, Integer> getter, BiConsumer<IBaseSpawner, Integer> setter) {
			super(id, getter, setter);
		}

		@Override
		public Integer parseValue(JsonElement value) {
			return value == null ? 0 : value.getAsInt();
		}

		@Override
		public boolean apply(Integer value, Integer min, Integer max, IBaseSpawner spawner) {
			int old = getter.apply(spawner);
			setter.accept(spawner, Mth.clamp(old + value, min, max));
			return old != getter.apply(spawner);
		}

		@Override
		public Class<Integer> getTypeClass() {
			return Integer.class;
		}
	}

}
