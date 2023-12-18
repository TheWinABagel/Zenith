package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class PickaxeCategory extends EnchantmentCategoryMixin5 {
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof PickaxeItem;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin5 {
    @Shadow
    abstract boolean canEnchant(Item item);
}