package dev.shadowsoffire.apotheosis.mixin.ench;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = EnchantmentUtils.class)
public class EnchantmentUtilsMixin {
    /**
     * Adds properly leveled books to the creative menu
     */
    @Redirect(method = "addAllBooks", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private static int apothModifyMaxLevel(Enchantment enchantment) {
        if (Apotheosis.enableEnch) return EnchHooks.getMaxLevel(enchantment);
        return enchantment.getMaxLevel();
    }
}
