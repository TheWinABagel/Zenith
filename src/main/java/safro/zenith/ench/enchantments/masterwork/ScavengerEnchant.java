package safro.zenith.ench.enchantments.masterwork;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.ench.EnchModule;

import java.util.ArrayList;
import java.util.Collection;

public class ScavengerEnchant extends Enchantment {

	public ScavengerEnchant() {
		super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
	}

	@Override
	public int getMinCost(int level) {
		return 55 + level * level * 12; // 57 / 103 / 163
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

	public static void drops(Player p, LivingEntity entity, Collection<ItemEntity> drops, DamageSource source) {
		if (p.level.isClientSide) return;
		int scavenger = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.SCAVENGER, p.getMainHandItem());
		if (scavenger > 0 && p.level.random.nextInt(100) < scavenger * 2.5F) {
			entity.captureDrops(new ArrayList<>());
			entity.dropFromLootTable(source, true);
			drops.addAll(entity.captureDrops(null));
		}
	}

}