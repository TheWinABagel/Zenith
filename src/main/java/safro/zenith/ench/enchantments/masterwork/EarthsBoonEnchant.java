package safro.zenith.ench.enchantments.masterwork;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import safro.zenith.Zenith;
import safro.zenith.api.enchant.TableApplicableEnchant;
import safro.zenith.ench.EnchModule;
import safro.zenith.util.ZenithUtil;

public class EarthsBoonEnchant extends Enchantment implements TableApplicableEnchant {

	public EarthsBoonEnchant() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinCost(int level) {
		return 60 + (level - 1) * 20;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return 200;
	}

	@Override
	public Component getFullname(int level) {
		return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof PickaxeItem;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return ZenithUtil.canApplyItem(this, stack);
	}

	public static void provideBenefits(Player player, BlockPos pos, BlockState state) {
		ItemStack stack = player.getMainHandItem();
		int level = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.EARTH_BOON, stack);
		if (player.level.isClientSide) return;
		if (state.is(BlockTags.BASE_STONE_OVERWORLD) && level > 0 && player.getRandom().nextFloat() <= 0.01F * level) {
			ItemStack newDrop = ZenithUtil.getRandom(Zenith.BOON_DROPS, player.getRandom());
			Block.popResource(player.level, pos, newDrop);
		}
	}
}