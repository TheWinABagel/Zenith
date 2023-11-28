package dev.shadowsoffire.apotheosis.ench.enchantments;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.Predicates;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InertEnchantment extends Enchantment implements CustomEnchantingTableBehaviorEnchantment {

    public static final EnchantmentCategory NULL = ClassTinkerers.getEnum(EnchantmentCategory.class, "NULL");;

    public InertEnchantment() {
        super(Rarity.VERY_RARE, NULL, new EquipmentSlot[0]);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMinCost(int level) {
        return 2000;
    }

    @Override
    public int getMaxCost(int level) {
        return 2001;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

}
