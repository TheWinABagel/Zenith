package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class AxeCategory extends EnchantmentCategoryMixin1 {
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof AxeItem;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin1 {
    @Shadow
    abstract boolean canEnchant(Item item);
}