package safro.apotheosis.ench.enchantments.corrupted;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.apotheosis.api.enchant.TableApplicableEnchant;
import safro.apotheosis.ench.EnchModule;
import safro.apotheosis.util.ApotheosisUtil;

public class LifeMendingEnchant extends Enchantment implements TableApplicableEnchant {
	public LifeMendingEnchant() {
		super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
	}

	@Override
	public int getMinCost(int level) {
		return 65 + (level - 1) * 35;
	}

	@Override
	public int getMaxCost(int level) {
		return this.getMinCost(level) + 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return ApotheosisUtil.canApplyItem(this, stack) || stack.getItem() instanceof ShieldItem;
	}

	@Override
	public Component getFullname(int level) {
		return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_RED);
	}

	private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

	public static float lifeMend(LivingEntity e, float amt) {
		if (e.level.isClientSide) return -1;
		if (amt <= 0F) return -1;
		for (EquipmentSlot slot : SLOTS) {
			ItemStack stack = e.getItemBySlot(slot);
			if (!stack.isEmpty() && stack.isDamaged()) {
				int level = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.LIFE_MENDING, stack);
				if (level <= 0) continue;
				float cost = 1.0F / (1 << level - 1);
				int maxRestore = Math.min(Mth.floor(amt / cost), stack.getDamageValue());
				float amount = (amt - maxRestore * cost);
				stack.setDamageValue(stack.getDamageValue() - maxRestore);
				return amount;
			}
		}
		return -1;
	}
}