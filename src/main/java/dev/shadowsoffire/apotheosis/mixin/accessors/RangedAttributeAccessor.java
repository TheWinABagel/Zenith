package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RangedAttribute.class)
public interface RangedAttributeAccessor {

    @Accessor("minValue")
    @Mutable
    void zenithAttributes_setMinValue(double minValue);

    @Accessor("maxValue")
    @Mutable
    void zenithAttributes_setMaxValue(double maxValue);
}
