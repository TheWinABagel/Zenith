package safro.zenith.ench.enchantments.masterwork;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.api.enchant.TableApplicableEnchant;
import safro.zenith.ench.EnchModule;
import safro.zenith.util.ZenithUtil;

public class GrowthSerumEnchant extends Enchantment implements TableApplicableEnchant {

	public GrowthSerumEnchant() {
		super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND });
	}

	@Override
	public int getMinCost(int pLevel) {
		return 55;
	}

	@Override
	public Component getFullname(int level) {
		return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ShearsItem;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return ZenithUtil.canApplyItem(this, stack);
	}

	public static void unshear(Sheep sheep, ItemStack shears) {
		if (EnchantmentHelper.getItemEnchantmentLevel(EnchModule.GROWTH_SERUM, shears) > 0 && sheep.getRandom().nextBoolean()) sheep.setSheared(false);
	}
}
