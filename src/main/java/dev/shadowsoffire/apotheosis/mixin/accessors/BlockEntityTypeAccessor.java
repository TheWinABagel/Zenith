package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor<T extends BlockEntity> {
    @Mutable
    @Accessor
    void setFactory(BlockEntityType.BlockEntitySupplier<? extends T> factory);
}
