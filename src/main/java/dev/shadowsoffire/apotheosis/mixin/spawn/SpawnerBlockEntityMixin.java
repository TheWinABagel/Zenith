package dev.shadowsoffire.apotheosis.mixin.spawn;

import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.mixin.accessors.BaseSpawnerAccessor;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import dev.shadowsoffire.apotheosis.spawn.spawner.LyingLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(SpawnerBlockEntity.class)
public abstract class SpawnerBlockEntityMixin extends BlockEntity implements IBaseSpawner {

    public SpawnerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Shadow public abstract void load(CompoundTag tag);
    
    @Unique
    public boolean ignoresPlayers = false;
    @Unique
    public boolean ignoresConditions = false;
    @Unique
    public boolean redstoneControl = false;
    @Unique
    public boolean ignoresLight = false;
    @Unique
    public boolean hasNoAI = false;
    @Unique
    public boolean silent = false;
    @Unique
    public boolean baby = false;
    @Final
    @Shadow
    @Mutable
    private BaseSpawner spawner;

    @Override
    public boolean zenith$getIgnorePlayers() {
        return ignoresPlayers;
    }

    @Override
    public void zenith$setIgnoresPlayers(boolean bool) {
        ignoresPlayers = bool;
    }
    
    @Override
    public boolean zenith$getIgnoresConditions() {
        return ignoresConditions;
    }

    @Override
    public void zenith$setIgnoresConditions(boolean bool) {
        ignoresConditions = bool;
    }

    @Override
    public boolean zenith$getRedstoneControl() {
        return redstoneControl;
    }

    @Override
    public void zenith$setRedstoneControl(boolean bool) {
        redstoneControl = bool;
    }

    @Override
    public boolean zenith$getIgnoreLight() {
        return ignoresLight;
    }
    @Override
    public void zenith$setIgnoreLight(boolean bool) {
        ignoresLight = bool;
    }
    
    @Override
    public boolean zenith$getNoAi() {
        return hasNoAI;
    }

    @Override
    public void zenith$setNoAi(boolean bool) {
        hasNoAI = bool;
    }

    @Override
    public boolean zenith$getSilent() {
        return silent;
    }

    @Override
    public void zenith$setSilent(boolean bool) {
        silent = bool;
    }

    @Override
    public boolean zenith$getBaby() {
        return baby;
    }

    @Override
    public void zenith$setBaby(boolean bool) {
        baby = bool;
    }

    @Override
    public BaseSpawner zenith$getSpawner() {
        return spawner;
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void load(CompoundTag tag, CallbackInfo ci) {
        this.ignoresPlayers = tag.getBoolean("ignore_players");
        this.ignoresConditions = tag.getBoolean("ignore_conditions");
        this.redstoneControl = tag.getBoolean("redstone_control");
        this.ignoresLight = tag.getBoolean("ignore_light");
        this.hasNoAI = tag.getBoolean("no_ai");
        this.silent = tag.getBoolean("silent");
        this.baby = tag.getBoolean("baby");
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("ignore_players", this.ignoresPlayers);
        tag.putBoolean("ignore_conditions", this.ignoresConditions);
        tag.putBoolean("redstone_control", this.redstoneControl);
        tag.putBoolean("ignore_light", this.ignoresLight);
        tag.putBoolean("no_ai", this.hasNoAI);
        tag.putBoolean("silent", this.silent);
        tag.putBoolean("baby", this.baby);
    }

    // Spawner logic override
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void zenithOverrideSpawnerLogic(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        this.spawner = new BaseSpawner() {
            @Override
            public void setEntityId(EntityType<?> type, @Nullable Level level, RandomSource rand, BlockPos pos) {
                ((BaseSpawnerAccessor) (Object) this).setNextSpawnData(new SpawnData());
                super.setEntityId(type, level, rand, pos);
                ((BaseSpawnerAccessor) (Object) this).setSpawnPotentials(SimpleWeightedRandomList.single(((BaseSpawnerAccessor) (BaseSpawner) this).getNextSpawnData()));
                if (level != null) this.delay(level, pos);
            }

            @Override
            public void broadcastEvent(Level level, BlockPos pos, int id) {
                level.blockEvent(pos, Blocks.SPAWNER, id, 0);
            }

            @Override
            public void setNextSpawnData(Level level, BlockPos pos, SpawnData nextSpawnData) {
                super.setNextSpawnData(level, pos, nextSpawnData);

                if (level != null) {
                    BlockState state = level.getBlockState(pos);
                    level.sendBlockUpdated(pos, state, state, 4);
                }
            }

            private boolean isActivated(Level level, BlockPos pos) {
                boolean hasPlayer = SpawnerBlockEntityMixin.this.ignoresPlayers || ((BaseSpawnerAccessor) (Object) this).callIsNearPlayer(level, pos);
                return hasPlayer && (!SpawnerBlockEntityMixin.this.redstoneControl || SpawnerBlockEntityMixin.this.level.hasNeighborSignal(pos));
            }

            private void delay(Level pLevel, BlockPos pPos) {
                if (((BaseSpawnerAccessor) (Object) this).getMaxSpawnDelay() <= ((BaseSpawnerAccessor) (Object) this).getMinSpawnDelay()) {
                    ((BaseSpawnerAccessor) (Object) this).setSpawnDelay(((BaseSpawnerAccessor) (Object) this).getMinSpawnDelay());
                } else {
                    ((BaseSpawnerAccessor) (Object) this).setSpawnDelay(
                            ((BaseSpawnerAccessor) (Object) this).getMinSpawnDelay() +
                                    pLevel.random.nextInt(((BaseSpawnerAccessor) (Object) this).getMaxSpawnDelay() - ((BaseSpawnerAccessor) (Object) this).getMinSpawnDelay()));
                }

                ((BaseSpawnerAccessor) (Object) this).getSpawnPotentials().getRandom(pLevel.random).ifPresent(potential -> {
                    this.setNextSpawnData(pLevel, pPos, potential.getData());
                });
                this.broadcastEvent(pLevel, pPos, 1);
            }

            @Override
            public void clientTick(@NotNull Level pLevel, @NotNull BlockPos pPos) {
                if (!this.isActivated(pLevel, pPos)) {
                    ((BaseSpawnerAccessor) (Object) this).setOSpin(((BaseSpawnerAccessor) (Object) this).getSpin());
                } else {
                    double d0 = pPos.getX() + pLevel.random.nextDouble();
                    double d1 = pPos.getY() + pLevel.random.nextDouble();
                    double d2 = pPos.getZ() + pLevel.random.nextDouble();
                    pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    pLevel.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    if (((BaseSpawnerAccessor) (Object) this).getSpawnDelay() > 0) {
                        ((BaseSpawnerAccessor) (Object) this).setSpawnDelay(((BaseSpawnerAccessor) (Object) this).getSpawnDelay() - 1);
                        //--this.spawnDelay;
                    }

                    ((BaseSpawnerAccessor) (Object) this).setOSpin(((BaseSpawnerAccessor) (Object) this).getSpin());
                    ((BaseSpawnerAccessor) (Object) this).setSpin((((BaseSpawnerAccessor) (Object) this).getSpin() + 1000.0F / (((BaseSpawnerAccessor) (Object) this).getSpawnDelay() + 200.0F)) % 360.0D);
                }

            }

            @Override
            public void serverTick(@NotNull ServerLevel level, @NotNull BlockPos pPos) {
                if (this.isActivated(level, pPos)) {
                    if (((BaseSpawnerAccessor) (Object) this).getSpawnDelay() == -1) {
                        this.delay(level, pPos);
                    }

                    if (((BaseSpawnerAccessor) (Object) this).getSpawnDelay() > 0) {
                        ((BaseSpawnerAccessor) (Object) this).setSpawnDelay(((BaseSpawnerAccessor) (Object) this).getSpawnDelay() - 1);
                    } else {
                        boolean flag = false;
                        RandomSource rand = level.getRandom();
                        SpawnData spawnData = ((BaseSpawnerAccessor) (Object) this).callGetOrCreateNextSpawnData(level, rand, pPos);

                        for (int i = 0; i < ((BaseSpawnerAccessor) (Object) this).getSpawnCount(); ++i) {
                            CompoundTag tag = spawnData.getEntityToSpawn();
                            EntityType<?> entityType = EntityType.by(tag).orElse(null);
                            if (entityType == null) {
                                this.delay(level, pPos);
                                return;
                            }

                            ListTag posList = tag.getList("Pos", 6);
                            int size = posList.size();
                            double x = size >= 1 ? posList.getDouble(0) : pPos.getX() + (rand.nextDouble() - rand.nextDouble()) * ((BaseSpawnerAccessor) (Object) this).getSpawnRange() + 0.5D;
                            double y = size >= 2 ? posList.getDouble(1) : (double) (pPos.getY() + rand.nextInt(3) - 1);
                            double z = size >= 3 ? posList.getDouble(2) : pPos.getZ() + (rand.nextDouble() - rand.nextDouble()) * ((BaseSpawnerAccessor) (Object) this).getSpawnRange() + 0.5D;
                            if (level.noCollision(entityType.getAABB(x, y, z))) {
                                BlockPos blockpos = BlockPos.containing(x, y, z);

                                // LOGIC CHANGE : Ability to ignore conditions set in the spawner and by the entity.
                                LyingLevel liar = new LyingLevel(level);
                                boolean useLiar = false;
                                if (!SpawnerBlockEntityMixin.this.ignoresConditions) {
                                    if (SpawnerBlockEntityMixin.this.ignoresLight) {
                                        boolean pass = false;
                                        for (int light = 0; light < 16; light++) {
                                            liar.setFakeLightLevel(light);
                                            if (this.checkSpawnRules(spawnData, entityType, liar, blockpos)) {
                                                pass = true;
                                                break;
                                            }
                                        }
                                        if (!pass) continue;
                                        else useLiar = true;
                                    } else if (!this.checkSpawnRules(spawnData, entityType, level, blockpos)) continue;
                                }

                                Entity entity = EntityType.loadEntityRecursive(tag, level, freshEntity -> {
                                    freshEntity.moveTo(x, y, z, freshEntity.getYRot(), freshEntity.getXRot());
                                    return freshEntity;
                                });

                                if (entity == null) {
                                    this.delay(level, pPos);
                                    return;
                                }

                                // Raise the NoAI Flag and set the zenith:movable flag for the main mob and all mob passengers.
                                if (SpawnerBlockEntityMixin.this.hasNoAI) {
                                    entity.getSelfAndPassengers().filter(Mob.class::isInstance).map(Mob.class::cast).forEach(mob -> {
                                        mob.setNoAi(true);
                                        ZenithComponents.MOVABLE.get(mob).setValue(true);
                                    });
                                }

                                if (SpawnerBlockEntityMixin.this.baby && entity instanceof Mob mob) {
                                    mob.setBaby(true);
                                }

                                if (SpawnerBlockEntityMixin.this.silent) entity.setSilent(true);

                                int nearby = level.getEntitiesOfClass(entity.getClass(), new AABB(pPos.getX(), pPos.getY(), pPos.getZ(), pPos.getX() + 1, pPos.getY() + 1, pPos.getZ() + 1).inflate(((BaseSpawnerAccessor) (Object) this).getSpawnRange())).size();
                                if (nearby >= ((BaseSpawnerAccessor) (Object) this).getMaxNearbyEntities()) {
                                    this.delay(level, pPos);
                                    return;
                                }

                                entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), rand.nextFloat() * 360.0F, 0.0F);
                                if (entity instanceof Mob mob) {
                                    if (spawnData.getCustomSpawnRules().isEmpty() && !mob.checkSpawnRules(useLiar ? liar : level, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(level)) {
                                        continue;
                                    }

                                    if (spawnData.getEntityToSpawn().size() == 1 && spawnData.getEntityToSpawn().contains("id", 8)) {
                                        ((Mob) entity).finalizeSpawn(useLiar ? liar : level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData) null, (CompoundTag) null);

                                    }
                                }

                                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                                    this.delay(level, pPos);
                                    return;
                                }

                                level.levelEvent(LevelEvent.PARTICLES_MOBBLOCK_SPAWN, pPos, 0);
                                if (entity instanceof Mob) {
                                    ((Mob) entity).spawnAnim();
                                }

                                flag = true;
                            }
                        }

                        if (flag) {
                            this.delay(level, pPos);
                        }

                    }
                }
            }

            /**
             * Checks if the requested entity passes spawn rule checks or not.
             */
            private boolean checkSpawnRules(SpawnData spawnData, EntityType<?> entityType, ServerLevelAccessor pServerLevel, BlockPos blockpos) {
                if (spawnData.getCustomSpawnRules().isPresent()) {
                    if (!entityType.getCategory().isFriendly() && pServerLevel.getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }

                    SpawnData.CustomSpawnRules customRules = spawnData.getCustomSpawnRules().get();
                    if (SpawnerBlockEntityMixin.this.ignoresLight)
                        return true; // All custom spawn rules are light-based, so if we ignore light, we can short-circuit here.
                    if (!customRules.blockLightLimit().isValueInRange(pServerLevel.getBrightness(LightLayer.BLOCK, blockpos))
                            || !customRules.skyLightLimit().isValueInRange(pServerLevel.getBrightness(LightLayer.SKY, blockpos))) {
                        return false;
                    }
                } else if (!SpawnPlacements.checkSpawnRules(entityType, pServerLevel, MobSpawnType.SPAWNER, blockpos, pServerLevel.getRandom())) {
                    return false;
                }
                return true;
            }

        };
    }
}
