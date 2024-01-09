package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccessor {
    @Accessor
    @Mutable
    static Map<ResourceLocation, CriterionTrigger<?>> getCRITERIA() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setINVENTORY_CHANGED(InventoryChangeTrigger INVENTORY_CHANGED) {
        throw new UnsupportedOperationException();
    }
}
