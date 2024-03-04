package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class StringComponent implements Component, AutoSyncedComponent {
    protected final String name;
    private String value;
    public StringComponent(String name) {
        this.name = name;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        value = tag.getString(name);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putString(name, value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
