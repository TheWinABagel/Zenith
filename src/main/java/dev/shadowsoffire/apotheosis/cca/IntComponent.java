package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public class IntComponent implements Component {
    protected int value = 0;
    protected String name;

    public IntComponent(String name) {
        this.name = name;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        value = tag.getInt(name);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt(name, value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
