package dev.shadowsoffire.apotheosis.mixin.ench;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.apotheosis.ench.table.RealEnchantmentHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(value = EnchantmentHelper.class, priority = 2000)
public class EnchantmentHelperMixin {

    /**
     * @param level         The current enchanting power.
     * @param stack         The ItemStack being enchanted.
     * @param allowTreasure If treasure enchantments are allowed.
     * @author Shadows
     * @reason Enables Zenith special handling of enchanting rules.
     */
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void zenith_getAvailableEnchantmentResults(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (Apotheosis.enableEnch) {
            cir.setReturnValue(RealEnchantmentHelper.getAvailableEnchantmentResults(level, stack, allowTreasure, Collections.emptySet()));
        }
    }

    /**
     * @param random        The random
     * @param itemStack     The stack being enchanted
     * @param level         The enchanting level
     * @param allowTreasure If treasure enchantments are allowed.
     * @author Shadows
     * @reason Enables global consistency with the Zenith enchanting system, even outside the table.
     */
    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true)
    private static void zenith_selectEnchantment(RandomSource random, ItemStack itemStack, int level, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (Apotheosis.enableEnch) {
            cir.setReturnValue(RealEnchantmentHelper.selectEnchantment(random, itemStack, level, 15F, 0, 0, allowTreasure, Collections.emptySet()));
        }
    }

    @WrapOperation(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isTreasureOnly()Z"))
    private static boolean zenith_redirectTreasureOnly(Enchantment ench, Operation<Boolean> original) {
        return EnchHooks.isTreasureOnly(ench);
    }

    @WrapOperation(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isDiscoverable()Z"))
    private static boolean zenith_redirectDiscoverable(Enchantment ench, Operation<Boolean> original) {
        return EnchHooks.isDiscoverable(ench);
    }

    @WrapOperation(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private static int zenith_redirectMaxLevel(Enchantment ench, Operation<Boolean> original) {
        return EnchHooks.getMaxLevel(ench);
    }
}
