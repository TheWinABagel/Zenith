package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.AnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class NullCategory extends EnchantmentCategoryMixin4 {
    @Override
    public boolean canEnchant(Item item) {
        return false;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin4 {
    @Shadow
    abstract boolean canEnchant(Item item);
}
