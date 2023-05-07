package safro.zenith.ench.enchantments;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.zenith.api.enchant.TableApplicableEnchant;
import safro.zenith.ench.EnchModule;
import safro.zenith.util.ZenithUtil;

public class ReflectiveEnchant extends Enchantment implements TableApplicableEnchant {

	public ReflectiveEnchant() {
		super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND });
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return enchantmentLevel * 18;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return 200;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ShieldItem;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return ZenithUtil.canApplyItem(this, stack);
	}

	/**
	 * Enables application of the reflective defenses enchantment.
	 * Called from {@link }
	 */
	public static void reflect(LivingEntity user, DamageSource source, float blockDamage) {
		Entity attacker = source.getDirectEntity();
		ItemStack shield = user.getUseItem();
		int level;
		if ((level = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.REFLECTIVE, shield)) > 0) {
			if (user.level.random.nextInt(Math.max(2, 7 - level)) == 0) {
				DamageSource src = user instanceof Player plr ? DamageSource.playerAttack(plr).setMagic().bypassArmor() : DamageSource.MAGIC;
				if (attacker instanceof LivingEntity livingAttacker) {
					livingAttacker.hurt(src, level * 0.15F * blockDamage);
					shield.hurtAndBreak(10, user, ent -> {
						ent.broadcastBreakEvent(EquipmentSlot.OFFHAND);
					});
				}
			}
		}
	}

}