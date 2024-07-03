package dev.shadowsoffire.apotheosis.spawn.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModule;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
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
        boolean supr = CustomEnchantingTableBehaviorEnchantment.super.canApplyAtEnchantingTable(stack);
        if (Apotheosis.enableEnch) {
            supr |= EnchModule.AXE.canEnchant(stack.getItem());
        }
        return supr;
    }

    public static void handleCapturing(LivingEntity killed, DamageSource source, Collection<ItemEntity> drops) {
        Entity killer = source.getEntity();
        if (killer instanceof LivingEntity living) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(SpawnerModule.CAPTURING, living.getMainHandItem());
            if (level <= 0) return;
            if (SpawnerModule.bannedMobs.contains(EntityType.getKey(killed.getType()))) return;
            if (killed.level().random.nextFloat() < level / 250F) {
                if (enableDebug) SpawnerModule.LOG.info(String.valueOf(killed.getType()));
                Item eggItem = SpawnEggItem.byId(killed.getType());
                if (eggItem == null) return;
                ItemStack egg = new ItemStack(eggItem);
                if (enableDebug) SpawnerModule.LOG.info("Dropping egg of type: {}", egg);
                drops.add(new ItemEntity(killed.level(), killed.getX(), killed.getY(), killed.getZ(), egg));
            }
        }
    }

}
