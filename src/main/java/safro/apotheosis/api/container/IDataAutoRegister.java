package safro.apotheosis.api.container;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.Consumer;

public interface IDataAutoRegister {
    public void registerSlots(Consumer<DataSlot> consumer);
}
