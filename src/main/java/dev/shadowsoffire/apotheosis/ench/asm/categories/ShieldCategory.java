package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class ShieldCategory extends EnchantmentCategoryMixin {
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof ShieldItem;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin7 {
    @Shadow
    abstract boolean canEnchant(Item item);
}