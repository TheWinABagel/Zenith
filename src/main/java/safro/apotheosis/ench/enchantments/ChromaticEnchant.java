package safro.apotheosis.ench.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import safro.apotheosis.api.enchant.TableApplicableEnchant;

public class ChromaticEnchant extends Enchantment implements TableApplicableEnchant {

	public ChromaticEnchant() {
		super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND });
	}

	@Override
	public int getMinCost(int pLevel) {
		return 40;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ShearsItem;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return this.canEnchant(stack);
	}

}
