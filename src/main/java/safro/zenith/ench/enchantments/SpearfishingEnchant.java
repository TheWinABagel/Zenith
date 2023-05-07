package safro.zenith.ench.enchantments;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.Zenith;
import safro.zenith.ench.EnchModule;
import safro.zenith.ench.EnchModuleEvents;
import safro.zenith.util.ZenithUtil;

import java.util.Collection;

public class SpearfishingEnchant extends Enchantment {

	public SpearfishingEnchant() {
		super(Rarity.UNCOMMON, EnchantmentCategory.TRIDENT, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
	}

	/**
	* Returns the minimal value of enchantability needed on the enchantment level passed.
	*/
	@Override
	public int getMinCost(int pEnchantmentLevel) {
		return 12 + (pEnchantmentLevel - 1) * 18;
	}

	@Override
	public int getMaxCost(int pEnchantmentLevel) {
		return 200;
	}

	/**
	* Returns the maximum level that the enchantment can have.
	*/
	@Override
	public int getMaxLevel() {
		return 5;
	}

	public static void addFishes(LivingEntity dead, Collection<ItemEntity> drops, DamageSource src) {
		if (src.getDirectEntity() instanceof ThrownTrident trident) {
			if (trident.level.isClientSide) return;
			ItemStack triStack = ((EnchModuleEvents.TridentGetter) trident).getTridentItem();
			int level = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.SPEARFISHING, triStack);
			if (trident.random.nextFloat() < 3.5F * level) {
				ItemStack spearDrops = ZenithUtil.getRandom(Zenith.SPEARFISHING_DROPS, trident.random);
				spearDrops.setCount(1 + trident.random.nextInt(3));
				drops.add(new ItemEntity(trident.level, dead.getX(), dead.getY(), dead.getZ(), spearDrops));
			}
		}
	}
}