package safro.zenith.ench.enchantments;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import safro.zenith.ench.EnchModule;

public class NaturesBlessingEnchant extends Enchantment {

	public NaturesBlessingEnchant() {
		super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[0]);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof HoeItem;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinCost(int level) {
		return 25 + level * 10;
	}

	@Override
	public int getMaxCost(int level) {
		return 200;
	}

	public static InteractionResult rightClick(ItemStack s, Player player, Level world, BlockPos pos, InteractionHand hand) {
		int nbLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.NATURES_BLESSING, s);
		if (!player.isShiftKeyDown() && nbLevel > 0 && BoneMealItem.growCrop(s.copy(), world, pos)) {
			s.hurtAndBreak(Math.max(1, 6 - nbLevel), player, ent -> ent.broadcastBreakEvent(hand));
			return InteractionResult.SUCCESS;
		}
		return null;
	}
}