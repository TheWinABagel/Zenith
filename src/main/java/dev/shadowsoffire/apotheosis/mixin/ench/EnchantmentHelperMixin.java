package dev.shadowsoffire.apotheosis.mixin.ench;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.table.RealEnchantmentHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EnchantmentHelper.class, priority = 1100)
public class EnchantmentHelperMixin {

    /**
     * @param level         The current enchanting power.
     * @param stack         The ItemStack being enchanted.
     * @param allowTreasure If treasure enchantments are allowed.
     * @author Shadows
     * @reason Enables apotheosis special handling of enchanting rules. More lenient injection is not possible.
     */
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void getAvailableEnchantmentResults(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (!Apotheosis.enableEnch) return;
        cir.setReturnValue(RealEnchantmentHelper.getAvailableEnchantmentResults(level, stack, allowTreasure));
    }

    /**
     * @param random        The random
     * @param itemStack     The stack being enchanted
     * @param level         The enchanting level
     * @param allowTreasure If treasure enchantments are allowed.
     * @author Shadows
     * @reason Enables global consistency with the apotheosis enchanting system, even outside the table.
     */
    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true)
    private static void selectEnchantment(RandomSource random, ItemStack itemStack, int level, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        cir.setReturnValue(RealEnchantmentHelper.selectEnchantment(random, itemStack, level, 15F, 0, 0, allowTreasure));
    }

}
