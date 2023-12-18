package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {
    @Accessor
    int getMinSpawnDelay();

    @Accessor
    int getMaxSpawnDelay();

    @Accessor
    int getSpawnCount();

    @Accessor
    Entity getDisplayEntity();

    @Accessor
    int getMaxNearbyEntities();

    @Accessor
    int getRequiredPlayerRange();

    @Accessor
    int getSpawnRange();

    @Accessor
    void setMinSpawnDelay(int minSpawnDelay);

    @Accessor
    void setMaxSpawnDelay(int maxSpawnDelay);

    @Accessor
    void setSpawnCount(int spawnCount);

    @Accessor
    void setDisplayEntity(Entity displayEntity);

    @Accessor
    void setMaxNearbyEntities(int maxNearbyEntities);

    @Accessor
    void setRequiredPlayerRange(int requiredPlayerRange);

    @Accessor
    void setSpawnRange(int spawnRange);

    @Accessor
    SimpleWeightedRandomList<SpawnData> getSpawnPotentials();

    @Accessor
    void setSpawnPotentials(SimpleWeightedRandomList<SpawnData> spawnPotentials);

    @Invoker
    void callSetNextSpawnData(@Nullable Level level, BlockPos pos, SpawnData nextSpawnData);

    @Accessor
    int getSpawnDelay();

    @Accessor
    void setSpawnDelay(int spawnDelay);

    @Accessor
    void setNextSpawnData(SpawnData nextSpawnData);

    @Accessor
    SpawnData getNextSpawnData();

    @Invoker
    boolean callIsNearPlayer(Level level, BlockPos pos);

    @Accessor
    void setOSpin(double oSpin);

    @Accessor
    double getSpin();

    @Accessor
    void setSpin(double spin);

    @Invoker
    SpawnData callGetOrCreateNextSpawnData(@Nullable Level level, RandomSource random, BlockPos pos);
}
