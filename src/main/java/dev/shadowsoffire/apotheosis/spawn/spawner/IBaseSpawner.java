package dev.shadowsoffire.apotheosis.spawn.spawner;

import net.minecraft.world.level.BaseSpawner;

public interface IBaseSpawner {
    boolean zenith$getIgnorePlayers();
    boolean zenith$getIgnoresConditions();
    boolean zenith$getRedstoneControl();
    boolean zenith$getIgnoreLight();
    boolean zenith$getNoAi();
    boolean zenith$getSilent();
    boolean zenith$getBaby();
    void zenith$setIgnoresPlayers(boolean flag);
    void zenith$setIgnoresConditions(boolean flag);
    void zenith$setRedstoneControl(boolean flag);
    void zenith$setIgnoreLight(boolean flag);
    void zenith$setNoAi(boolean flag);
    void zenith$setSilent(boolean flag);
    void zenith$setBaby(boolean flag);
    BaseSpawner zenith$getSpawner();
}
