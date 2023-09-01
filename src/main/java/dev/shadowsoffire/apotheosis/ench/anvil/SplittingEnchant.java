package dev.shadowsoffire.apotheosis.ench.anvil;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SplittingEnchant extends Enchantment implements CustomEnchantingTableBehaviorEnchantment{

    public SplittingEnchant() {
        super(Rarity.RARE, EnchModule.ANVIL, new EquipmentSlot[0]);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return CustomEnchantingTableBehaviorEnchantment.super.canApplyAtEnchantingTable(stack) || stack.is(ItemTags.ANVIL);
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 20;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 200;
    }

    @Override
    public int getMaxLevel() {
        return 1;
}

}
