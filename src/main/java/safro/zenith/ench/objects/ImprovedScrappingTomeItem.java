package safro.zenith.ench.objects;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import safro.zenith.Zenith;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImprovedScrappingTomeItem extends BookItem {
	static Random rand = new Random();

	public ImprovedScrappingTomeItem() {
		super(new Properties().tab(Zenith.ZENITH_GROUP));
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.isEnchanted()) return;
		tooltip.add(Component.translatable("info.zenith.improved_scrap_tome").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("info.zenith.improved_scrap_tome2").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return !stack.isEnchanted() ? super.getRarity(stack) : Rarity.UNCOMMON;
	}

	public static Pair<ItemStack, List<Integer>> updateAnvil(ItemStack weapon, ItemStack book, Player player) {
		if (!(book.getItem() instanceof ImprovedScrappingTomeItem) || book.isEnchanted() || !weapon.isEnchanted()) return null;

		Map<Enchantment, Integer> wepEnch = EnchantmentHelper.getEnchantments(weapon);
		ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantmentHelper.setEnchantments(wepEnch, out);
		return new Pair<>(out, List.of(wepEnch.size() * 10, 1));
	}
}