package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentMenu.class)
public interface EnchantmentMenuAccessor {
    @Accessor
    Container getEnchantSlots();

    @Mutable
    @Accessor
    void setEnchantSlots(Container enchantSlots);

    @Accessor
    ContainerLevelAccess getAccess();

    @Accessor
    DataSlot getEnchantmentSeed();

    @Mutable
    @Accessor
    void setEnchantmentSeed(DataSlot enchantmentSeed);

    @Accessor
    RandomSource getRandom();
}
