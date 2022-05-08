package safro.apotheosis.ench.enchantments.twisted;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import safro.apotheosis.ench.EnchModule;
import safro.apotheosis.mixin.BlockStateBaseAccessor;

public class MinersFervorEnchant extends DiggingEnchantment {

	public MinersFervorEnchant() {
		super(Rarity.RARE, EquipmentSlot.MAINHAND);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 45 + (enchantmentLevel - 1) * 30;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return this.getMinCost(enchantmentLevel) + 50;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public Component getFullname(int level) {
		return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_PURPLE);
	}

	@Override
	protected boolean checkCompatibility(Enchantment e) {
		return super.checkCompatibility(e) && e != Enchantments.BLOCK_EFFICIENCY;
	}

	public static float breakSpeed(Player p, BlockState state, float original) {
		ItemStack stack = p.getMainHandItem();
		if (stack.isEmpty()) return -1;
		int level = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.MINERS_FERVOR, stack);
		if (level > 0) {
			if (stack.getDestroySpeed(state) > 1.0F) {
				float hardness = ((BlockStateBaseAccessor)state).getDestroySpeed();
				return Math.max(original, Math.min(29.9999F, 7.5F + 4.5F * level) * hardness);
			}
		}
		return -1;
	}
}