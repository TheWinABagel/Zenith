package dev.shadowsoffire.apotheosis.mixin.compat.soulsweapons.present;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.items.TrickWeapon;
import net.soulsweaponry.registry.WeaponRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Credits to MariumBacchus for the original code in Soulslike Weaponry, licensed under CC0 1.0 Universal
 * This is needed as both mods are redirecting the same thing
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperSoulslikeWeaponryMixin {

    @Unique
    private static Enchantment currentEnchantment;

    @WrapOperation(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.isDiscoverable ()Z"))
    private static boolean isAvailableForRandomSelection(Enchantment enchantment, Operation<Boolean> original) {
        currentEnchantment = enchantment;
        return enchantment.isDiscoverable();
    }

    @Redirect(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/EnchantmentCategory.canEnchant(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean isAcceptableItem(EnchantmentCategory enchantmentTarget, Item item) {
        if (enchantmentTarget == EnchantmentCategory.BOW) {
            return currentEnchantment.canEnchant(item.getDefaultInstance());
        }
        return enchantmentTarget.canEnchant(item);
    }

    @Inject(method = "getDamageBonus", at = @At("TAIL"), cancellable = true)
    private static void interceptGetDamage(ItemStack stack, MobType group, CallbackInfoReturnable<Float> cir) {
        if (stack.is(WeaponRegistry.STING) && group == MobType.ARTHROPOD) {
            float value = cir.getReturnValue() + ConfigConstructor.sting_bonus_arthropod_damage;
            cir.setReturnValue(value);
        }
        if (group == MobType.UNDEAD && (stack.getItem() instanceof TrickWeapon && ((TrickWeapon) stack.getItem()).hasUndeadBonus() || stack.is(WeaponRegistry.MASTER_SWORD))) {
            float value = cir.getReturnValue() + ConfigConstructor.righteous_undead_bonus_damage + (float) EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
            cir.setReturnValue(value);
        }
    }
}
