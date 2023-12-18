package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class CoreArmorCategory extends EnchantmentCategoryMixin2 {
    @Override
    public boolean canEnchant(Item item) {
        return EnchantmentCategory.ARMOR_CHEST.canEnchant(item) || EnchantmentCategory.ARMOR_LEGS.canEnchant(item);
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin2 {
    @Shadow
    abstract boolean canEnchant(Item item);
}