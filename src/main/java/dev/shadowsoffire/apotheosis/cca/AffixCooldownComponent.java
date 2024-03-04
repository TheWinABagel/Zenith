package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class AffixCooldownComponent implements Component, AutoSyncedComponent {
    private static Map<String, Long> values = new HashMap<>();

    public AffixCooldownComponent() {
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        for (String name : tag.getAllKeys()) {
            values.put(name, tag.getLong(name));
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        values.forEach(tag::putLong);
    }

    public void setValue(String id, long value) {
        values.put(id, value);
    }

    public long getValue(String id) {
        if (values.get(id) == null) {
            values.put(id, 0L);
            return 0;
        }
        return values.get(id);
    }

}
