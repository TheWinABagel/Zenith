package safro.zenith.ench.enchantments;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.api.enchant.TableApplicableEnchant;
import safro.zenith.util.ApotheosisUtil;

import java.util.Map;

public class ShieldBashEnchant extends Enchantment implements TableApplicableEnchant {

	public ShieldBashEnchant() {
		super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 1 + (enchantmentLevel - 1) * 17;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return this.getMinCost(enchantmentLevel) + 40;
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ShieldItem;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return ApotheosisUtil.canApplyItem(this, stack);
	}

	@Override
	public float getDamageBonus(int pLevel, MobType pType) {
		return 3.5F * pLevel;
	}

	@Override
	public void doPostAttack(LivingEntity user, Entity target, int level) {
		if (target instanceof LivingEntity) {
			Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(this, user);
			if (entry != null) {
				entry.getValue().hurtAndBreak(Math.max(1, 20 - level), user, e -> {
					e.broadcastBreakEvent(entry.getKey());
				});
			}
		}
	}

}