package safro.zenith.ench.enchantments.corrupted;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.ench.CorruptedDamageSource;

public class BerserkersFuryEnchant extends Enchantment {

	public BerserkersFuryEnchant() {
		super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] { EquipmentSlot.CHEST });
	}

	@Override
	public int getMinCost(int level) {
		return 50 + level * 40;
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
	public boolean isCurse() {
		return true;
	}

	@Override
	public Component getFullname(int level) {
		return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_RED);
	}

	/**
	 * Handles the application of Berserker's Fury.
	 */
	public void livingHurt() {
		LivingEntityEvents.ACTUALLY_HURT.register((source, user, amount) -> {
			if (source.getEntity() instanceof Entity && user.getEffect(MobEffects.DAMAGE_RESISTANCE) == null) {
				int level = EnchantmentHelper.getEnchantmentLevel(this, user);
				if (level > 0) {
					user.invulnerableTime = 0;
					user.hurt(CorruptedDamageSource.DEFAULT, (float) Math.pow(2.5, level));
					user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 500, level - 1));
					user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, level - 1));
					user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, level - 1));
				}
			}
			return amount;
		});
	}
}