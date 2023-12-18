package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Enchantment.class)
public interface EnchantmentAccessor {
    @Mutable
    @Accessor
    void setRarity(Enchantment.Rarity rarity);
}
