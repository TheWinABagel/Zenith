package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class ShearsCategory extends EnchantmentCategoryMixin6 {
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof ShearsItem;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin6 {
    @Shadow
    abstract boolean canEnchant(Item item);
}