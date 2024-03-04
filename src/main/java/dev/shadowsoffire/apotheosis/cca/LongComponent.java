package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class LongComponent implements Component {
    protected String name;
    private long value = 0;
    public LongComponent(String name) {
        this.name = name;
    }

    public LongComponent() {
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        List<String> list = new ArrayList<>();

        value = tag.getLong(name);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putLong(name, value);
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
