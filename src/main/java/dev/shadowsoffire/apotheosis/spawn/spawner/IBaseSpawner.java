package dev.shadowsoffire.apotheosis.spawn.spawner;

import net.minecraft.world.level.BaseSpawner;

public interface IBaseSpawner {
    boolean getIgnorePlayers();
    boolean getIgnoresConditions();
    boolean getRedstoneControl();
    boolean getIgnoreLight();
    boolean getNoAi();
    boolean getSilent();
    boolean getBaby();
    void setIgnoresPlayers(boolean flag);
    void setIgnoresConditions(boolean flag);
    void setRedstoneControl(boolean flag);
    void setIgnoreLight(boolean flag);
    void setNoAi(boolean flag);
    void setSilent(boolean flag);
    void setBaby(boolean flag);
    BaseSpawner getSpawner();
}
