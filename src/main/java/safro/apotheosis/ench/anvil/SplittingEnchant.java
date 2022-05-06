package safro.apotheosis.ench.anvil;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.AnvilBlock;

public class SplittingEnchant extends Enchantment {

	public SplittingEnchant() {
		super(Rarity.RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[0]);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof AnvilBlock;
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