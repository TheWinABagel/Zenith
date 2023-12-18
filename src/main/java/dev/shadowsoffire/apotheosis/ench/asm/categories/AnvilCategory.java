package dev.shadowsoffire.apotheosis.ench.asm.categories;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.AnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class AnvilCategory extends EnchantmentCategoryMixin {
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof AnvilBlock;
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin {
    @Shadow
    abstract boolean canEnchant(Item item);
}
