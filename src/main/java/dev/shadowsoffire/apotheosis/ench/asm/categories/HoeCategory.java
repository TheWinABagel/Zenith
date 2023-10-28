package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class HoeCategory extends EnchantmentCategoryMixin3 {
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof HoeItem;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin3 {
    @Shadow
    abstract boolean canEnchant(Item item);
}
