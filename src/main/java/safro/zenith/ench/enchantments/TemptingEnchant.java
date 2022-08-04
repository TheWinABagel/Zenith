package safro.zenith.ench.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.ench.EnchModule;

public class TemptingEnchant extends Enchantment {

	public TemptingEnchant() {
		super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[0]);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 0;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return 200;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof HoeItem;
	}

	/**
	 * Allows checking if an item with Tempting can make an animal follow.
	 * Called from {@link }
	 * Injected by {@link }
	 */
	public static boolean shouldFollow(LivingEntity target) {
		ItemStack stack = target.getMainHandItem();
		if (EnchantmentHelper.getItemEnchantmentLevel(EnchModule.TEMPTING, stack) > 0) return true;
		stack = target.getOffhandItem();
		return EnchantmentHelper.getItemEnchantmentLevel(EnchModule.TEMPTING, stack) > 0;
	}

}