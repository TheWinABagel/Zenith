package dev.shadowsoffire.apotheosis.ench.enchantments;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class TemptingEnchant extends Enchantment {

    public TemptingEnchant() {
        super(Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, new EquipmentSlot[0]);
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 0;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 200;
    }

    /**
     * Allows checking if an item with Tempting can make an animal follow.
     * Called from {link TemptGoal#shouldFollow(LivingEntity)}
     * Injected by {link TemptGoalMixin}
     */
    public boolean shouldFollow(LivingEntity target) {
        ItemStack stack = target.getMainHandItem();
        if (EnchantmentHelper.getItemEnchantmentLevel(this, stack) > 0) return true;
        stack = target.getOffhandItem();
        return EnchantmentHelper.getItemEnchantmentLevel(this, stack) > 0;
    }

}
