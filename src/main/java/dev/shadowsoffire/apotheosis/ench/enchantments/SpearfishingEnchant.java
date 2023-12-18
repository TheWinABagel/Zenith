package dev.shadowsoffire.apotheosis.ench.enchantments;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.EnchModuleEvents.TridentGetter;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import io.github.fabricators_of_create.porting_lib.tags.TagHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

    public void addFishes() {
        LivingEntityLootEvents.DROPS.register((target, src, drops, lootingLevel, recentlyHit) -> {
                if (src.getDirectEntity() instanceof ThrownTrident trident) {
                    if (trident.level().isClientSide || drops == null) return false;
                    ItemStack triStack = ((TridentGetter) trident).getTridentItem();
                    int level = EnchantmentHelper.getItemEnchantmentLevel(this, triStack);
                    if (target.getRandom().nextFloat() < 3.5F * level) {
                        drops.add(new ItemEntity(trident.level(), target.getX(), target.getY(), target.getZ(),
                                new ItemStack(TagHelper.getRandomElement(BuiltInRegistries.ITEM, Apoth.Tags.SPEARFISHING_DROPS, target.getRandom()).orElse(Items.AIR), 1 + target.getRandom().nextInt(3))));
                    }
                }

            return false;
        });

    }

}
