package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShearsItem.class)
public abstract class ShearsItemMixin extends Item implements CustomEnchantingBehaviorItem {

    public ShearsItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.category.canEnchant(stack.getItem());
        return ench.category.canEnchant(stack.getItem()) || ench == Enchantments.UNBREAKING || ench == Enchantments.BLOCK_EFFICIENCY || ench == Enchantments.BLOCK_FORTUNE;
    }

    @Override
    public int getEnchantmentValue() {
        return Apotheosis.enableEnch ? 15 : 0;
    }

}
