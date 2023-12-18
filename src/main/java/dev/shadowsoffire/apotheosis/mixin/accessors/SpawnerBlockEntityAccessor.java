package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnerBlockEntity.class)
public interface SpawnerBlockEntityAccessor {
    @Invoker
    void callSaveAdditional(CompoundTag tag);
}
