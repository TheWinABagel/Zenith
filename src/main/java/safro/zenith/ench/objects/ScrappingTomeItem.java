package safro.zenith.ench.objects;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import safro.zenith.Zenith;

public class ScrappingTomeItem extends BookItem {
	static Random rand = new Random();

	public ScrappingTomeItem() {
		super(new Item.Properties().tab(Zenith.APOTH_GROUP));
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.isEnchanted()) return;
		tooltip.add(Component.translatable("info.zenith.scrap_tome").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("info.zenith.scrap_tome2").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return !stack.isEnchanted() ? super.getRarity(stack) : Rarity.UNCOMMON;
	}

	public static Pair<ItemStack, List<Integer>> updateAnvil(ItemStack weapon, ItemStack book, Player player) {
		if (!(book.getItem() instanceof ScrappingTomeItem) || book.isEnchanted() || !weapon.isEnchanted()) return null;

		Map<Enchantment, Integer> wepEnch = EnchantmentHelper.getEnchantments(weapon);
		int size = Mth.ceil(wepEnch.size() / 2D);
		List<Enchantment> keys = Lists.newArrayList(wepEnch.keySet());
		long seed = 1831;
		for (Enchantment e : keys) {
			seed ^= Registry.ENCHANTMENT.getKey(e).hashCode();
		}
		seed ^= player.getEnchantmentSeed();
		rand.setSeed(seed);
		while (wepEnch.keySet().size() > size) {
			Enchantment lost = keys.get(rand.nextInt(keys.size()));
			wepEnch.remove(lost);
			keys.remove(lost);
		}
		ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
		EnchantmentHelper.setEnchantments(wepEnch, out);
		return new Pair<>(out, List.of(wepEnch.size() * 10, 1));
	}
}