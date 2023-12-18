package dev.shadowsoffire.apotheosis.ench.asm;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Methods injected by Javascript Coremods.
 *
 * @author Shadows
 */
public class EnchHooks {

    /**
     * Replaces the call to {@link Enchantment#getMaxLevel()} in various classes.
     */
    public static int getMaxLevel(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.getMaxLevel();
        return EnchModule.getEnchInfo(ench).getMaxLevel();
    }

    /**
     * Replaces the call to {@link Enchantment#getMaxLevel()} in loot-only classes.
     */
    public static int getMaxLootLevel(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.getMaxLevel();
        return EnchModule.getEnchInfo(ench).getMaxLootLevel();
    }

    /**
     * Replaces the call to {@link Enchantment#isTreasureOnly()} in various classes.
     */
    public static boolean isTreasureOnly(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.isTreasureOnly();
        return EnchModule.getEnchInfo(ench).isTreasure();
    }

    /**
     * Replaces the call to {@link Enchantment#isDiscoverable()} in various classes.
     */
    public static boolean isDiscoverable(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.isDiscoverable();
        return EnchModule.getEnchInfo(ench).isDiscoverable();
    }

    /**
     * Replaces the call to {@link Enchantment#isDiscoverable()} in loot-only classes.
     */
    public static boolean isLootable(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.isDiscoverable();
        return EnchModule.getEnchInfo(ench).isLootable();
    }

    /**
     * Replaces the call to {@link Enchantment#isTradeable()} in various classes.
     */
    public static boolean isTradeable(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.isTradeable();
        return EnchModule.getEnchInfo(ench).isTradeable();
    }

}
