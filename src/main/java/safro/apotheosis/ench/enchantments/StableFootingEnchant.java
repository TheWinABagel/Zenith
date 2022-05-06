package safro.apotheosis.ench.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.apotheosis.ench.EnchModule;

public class StableFootingEnchant extends Enchantment {

	public StableFootingEnchant() {
		super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET });
	}

	@Override
	public int getMinCost(int level) {
		return 40;
	}

	@Override
	public int getMaxCost(int level) {
		return 200;
	}

	public static float breakSpeed(Player p, float ori, float newSpeed) {
		if (!p.isOnGround() && EnchantmentHelper.getEnchantmentLevel(EnchModule.STABLE_FOOTING, p) > 0) {
			if (ori < newSpeed * 5) return newSpeed * 5F;
		}
		return -1;
	}

}