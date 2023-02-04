package safro.zenith.ench.enchantments.masterwork;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.ench.EnchModule;

import java.util.Collection;

public class KnowledgeEnchant extends Enchantment {

	public KnowledgeEnchant() {
		super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
	}

	@Override
	public int getMinCost(int level) {
		return 55 + (level - 1) * 45;
	}

	@Override
	public int getMaxCost(int level) {
		return 200;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public Component getFullname(int level) {
		return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
	}

	public static void drops(Player p, LivingEntity e, Collection<ItemEntity> drops) {
		int knowledge = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.KNOWLEDGE, p.getMainHandItem());
		if (knowledge > 0 && !(e instanceof Player)) {
			int items = 0;
			for (ItemEntity i : drops)
				items += i.getItem().getCount();
			if (items > 0) drops.clear();
			items *= knowledge * 25;
			while (items > 0) {
				int i = ExperienceOrb.getExperienceValue(items);
				items -= i;
				p.level.addFreshEntity(new ExperienceOrb(p.level, e.getX(), e.getY(), e.getZ(), i));
			}
		}
	}
}