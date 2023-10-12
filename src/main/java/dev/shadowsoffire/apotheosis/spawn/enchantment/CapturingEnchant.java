package dev.shadowsoffire.apotheosis.spawn.enchantment;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModule;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Collection;

import static dev.shadowsoffire.apotheosis.Apotheosis.enableDebug;

public class CapturingEnchant extends Enchantment implements CustomEnchantingTableBehaviorEnchantment {

    public CapturingEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinCost(int level) {
        return 28 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 15;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return CustomEnchantingTableBehaviorEnchantment.super.canApplyAtEnchantingTable(stack) || EnchModule.AXE.canEnchant(stack.getItem());
    }

    public static void handleCapturing(LivingEntity target, DamageSource source, Collection<ItemEntity> drops) {
        Entity killer = source.getEntity();
        if (killer instanceof LivingEntity living) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(PotionModule.CAPTURING, living.getMainHandItem());
            LivingEntity killed = target;
            if (level <= 0) return;
            if (enableDebug) SpawnerModule.LOG.info("Has capturing level: {}", level);
            if (SpawnerModule.bannedMobs.contains(EntityType.getKey(killed.getType()))) return;
            if (enableDebug) SpawnerModule.LOG.info("Mob is not banned");
            if (killed.level().random.nextFloat() < level / 250F) {
                if (enableDebug) SpawnerModule.LOG.info(String.valueOf(killed.getType()));
                ItemStack egg = new ItemStack(SpawnEggItem.byId(killed.getType()));
                if (enableDebug) SpawnerModule.LOG.info("Dropping egg of type: {}", egg);
                drops.add(new ItemEntity(killed.level(), killed.getX(), killed.getY(), killed.getZ(), egg));
            }
        }
    }

}
