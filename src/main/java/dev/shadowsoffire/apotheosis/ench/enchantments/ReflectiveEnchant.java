package dev.shadowsoffire.apotheosis.ench.enchantments;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import io.github.fabricators_of_create.porting_lib.entity.events.ShieldBlockEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ReflectiveEnchant extends Enchantment implements CustomEnchantingTableBehaviorEnchantment{

    public ReflectiveEnchant() {
        super(Rarity.RARE, EnchModule.SHIELD, new EquipmentSlot[] { EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND });
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
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return CustomEnchantingTableBehaviorEnchantment.super.canApplyAtEnchantingTable(stack) || stack.getItem() instanceof ShieldItem;
    }

    /**
     * Enables application of the reflective defenses enchantment.
     * Called from {link LivingEntity#blockUsingShield(LivingEntity)}
     */
    public void reflect() {
        ShieldBlockEvent.EVENT.register(e -> {
            LivingEntity user = e.getEntity();
            Entity attacker = e.getDamageSource().getDirectEntity();
            ItemStack shield = user.getUseItem();
            int level = EnchantmentHelper.getItemEnchantmentLevel(this, shield);
            if (level > 0) {
                if (user.level().random.nextInt(Math.max(2, 7 - level)) == 0) {
                    DamageSource src = user.level().damageSources().indirectMagic(user, user);
                    if (attacker instanceof LivingEntity livingAttacker) {
                        livingAttacker.hurt(src, level * 0.15F * e.getBlockedDamage());
                        shield.hurtAndBreak(10, user, ent -> {
                            ent.broadcastBreakEvent(EquipmentSlot.OFFHAND);
                        });
                    }
                }
            }
        });
    }

}
