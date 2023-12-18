package dev.shadowsoffire.apotheosis.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.mixin.accessors.BaseSpawnerAccessor;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerStats;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModifierTrigger implements CriterionTrigger<ModifierTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(Apotheosis.MODID, "spawner_modifier");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        Listeners ModifierTrigger$listeners = this.listeners.get(playerAdvancementsIn);
        if (ModifierTrigger$listeners == null) {
            ModifierTrigger$listeners = new Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, ModifierTrigger$listeners);
        }

        ModifierTrigger$listeners.add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        Listeners ModifierTrigger$listeners = this.listeners.get(playerAdvancementsIn);
        if (ModifierTrigger$listeners != null) {
            ModifierTrigger$listeners.remove(listener);
            if (ModifierTrigger$listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }

    }

    @Override
    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance createInstance(JsonObject json, DeserializationContext conditionsParser) {
        MinMaxBounds.Ints minDelay = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.MIN_DELAY.getId()));
        MinMaxBounds.Ints maxDelay = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.MAX_DELAY.getId()));
        MinMaxBounds.Ints spawnCount = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.SPAWN_COUNT.getId()));
        MinMaxBounds.Ints nearbyEnts = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.MAX_NEARBY_ENTITIES.getId()));
        MinMaxBounds.Ints playerRange = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.REQ_PLAYER_RANGE.getId()));
        MinMaxBounds.Ints spawnRange = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.SPAWN_RANGE.getId()));
        Boolean ignorePlayers = json.has(SpawnerStats.IGNORE_PLAYERS.getId()) ? json.get(SpawnerStats.IGNORE_PLAYERS.getId()).getAsBoolean() : null;
        Boolean ignoreConditions = json.has(SpawnerStats.IGNORE_CONDITIONS.getId()) ? json.get(SpawnerStats.IGNORE_CONDITIONS.getId()).getAsBoolean() : null;
        Boolean redstone = json.has(SpawnerStats.REDSTONE_CONTROL.getId()) ? json.get(SpawnerStats.REDSTONE_CONTROL.getId()).getAsBoolean() : null;
        Boolean ignoreLight = json.has(SpawnerStats.IGNORE_LIGHT.getId()) ? json.get(SpawnerStats.IGNORE_LIGHT.getId()).getAsBoolean() : null;
        Boolean noAI = json.has(SpawnerStats.NO_AI.getId()) ? json.get(SpawnerStats.NO_AI.getId()).getAsBoolean() : null;
        return new Instance(minDelay, maxDelay, spawnCount, nearbyEnts, playerRange, spawnRange, ignorePlayers, ignoreConditions, redstone, ignoreLight, noAI);
    }

    public void trigger(ServerPlayer player, SpawnerBlockEntity tile, SpawnerModifier modif) {
        Listeners ModifierTrigger$listeners = this.listeners.get(player.getAdvancements());
        if (ModifierTrigger$listeners != null) {
            ModifierTrigger$listeners.trigger(tile, modif);
        }

    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints minDelay;
        private final MinMaxBounds.Ints maxDelay;
        private final MinMaxBounds.Ints spawnCount;
        private final MinMaxBounds.Ints nearbyEnts;
        private final MinMaxBounds.Ints playerRange;
        private final MinMaxBounds.Ints spawnRange;
        private final Boolean ignorePlayers;
        private final Boolean ignoreConditions;
        private final Boolean redstone;
        private final Boolean ignoreLight;
        private final Boolean noAI;

        public Instance(MinMaxBounds.Ints minDelay, MinMaxBounds.Ints maxDelay, MinMaxBounds.Ints spawnCount, MinMaxBounds.Ints nearbyEnts, MinMaxBounds.Ints playerRange, MinMaxBounds.Ints spawnRange, Boolean ignorePlayers,
            Boolean ignoreConditions, Boolean redstone, Boolean ignoreLight, Boolean noAI) {
            super(ModifierTrigger.ID, ContextAwarePredicate.ANY);
            this.minDelay = minDelay;
            this.maxDelay = maxDelay;
            this.spawnCount = spawnCount;
            this.nearbyEnts = nearbyEnts;
            this.playerRange = playerRange;
            this.spawnRange = spawnRange;
            this.ignorePlayers = ignorePlayers;
            this.ignoreConditions = ignoreConditions;
            this.redstone = redstone;
            this.ignoreLight = ignoreLight;
            this.noAI = noAI;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializer) {
            return new JsonObject();
        }

        public boolean test(SpawnerBlockEntity tile, SpawnerModifier modif) {
            BaseSpawner logic = tile.getSpawner();
            IBaseSpawner spwn = (IBaseSpawner) tile;
            if (!this.minDelay.matches(((BaseSpawnerAccessor) logic).getMinSpawnDelay()) || !this.maxDelay.matches(((BaseSpawnerAccessor) logic).getMaxSpawnDelay()) || !this.spawnCount.matches(((BaseSpawnerAccessor) logic).getSpawnCount()) || !this.nearbyEnts.matches(((BaseSpawnerAccessor) logic).getMaxNearbyEntities())) return false;
            if (!this.playerRange.matches(((BaseSpawnerAccessor) logic).getRequiredPlayerRange())) return false;
            if (!this.spawnRange.matches(((BaseSpawnerAccessor) logic).getSpawnRange())) return false;
            if (this.ignorePlayers != null && spwn.zenith$getIgnorePlayers() != this.ignorePlayers) return false;
            if (this.ignoreConditions != null && spwn.zenith$getIgnoresConditions() != this.ignoreConditions) return false;
            if (this.redstone != null && spwn.zenith$getRedstoneControl() != this.redstone) return false;
            if (this.ignoreLight != null && spwn.zenith$getIgnoreLight() != this.ignoreLight) return false;
            if (this.noAI != null && spwn.zenith$getNoAi() != this.noAI) return false;
            return true;
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(Listener<Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(SpawnerBlockEntity tile, SpawnerModifier modif) {
            List<Listener<Instance>> list = null;

            for (Listener<Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(tile, modif)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (Listener<Instance> listener1 : list) {
                    listener1.run(this.playerAdvancements);
                }
            }

        }
    }
}
