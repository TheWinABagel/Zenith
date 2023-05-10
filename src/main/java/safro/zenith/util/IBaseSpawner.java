package safro.zenith.util;

import net.minecraft.world.level.BaseSpawner;

public interface IBaseSpawner {
    boolean getIgnoresPlayers();
    boolean getIgnoresConditions();
    boolean getRedstoneControl();
    boolean getIgnoreLight();
    boolean getNoAi();
    boolean getSilent();
    void setIgnoresPlayers(boolean flag);
    void setIgnoresConditions(boolean flag);
    void setRedstoneControl(boolean flag);
    void setIgnoreLight(boolean flag);
    void setNoAi(boolean flag);
    void setSilent(boolean flag);
    BaseSpawner getSpawner();
}
