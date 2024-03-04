package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import net.minecraft.nbt.CompoundTag;

public class AffixDataComponent implements Component, AutoSyncedComponent {
    private CompoundTag entityTag;

    public AffixDataComponent() {
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        tag.put(AffixHelper.AFFIX_DATA, entityTag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        entityTag = tag.getCompound(AffixHelper.AFFIX_DATA);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AffixDataComponent data && data.entityTag.equals(entityTag);
    }

    public CompoundTag getEntityTag() {
        return entityTag;
    }

    public void setEntityTag(CompoundTag entityTag) {
        this.entityTag = entityTag;
    }
}
