package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/EnchantmentHelper.getItemEnchantmentLevel (Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I", ordinal = 0))
    private int endlessQuiver(Enchantment enchantment, ItemStack stack, Operation<Integer> original) {
        int infinityLevel = original.call(enchantment, stack);
        if (!Apotheosis.enableEnch) return infinityLevel;
        int endlessLevel = EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.ENDLESS_QUIVER, stack);
        return endlessLevel > 0 ? endlessLevel : infinityLevel;
    }

    @WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean zenithEndlessQuiver(ItemStack instance, Item item, Operation<Boolean> original, @Local(argsOnly = true) ItemStack stack) {
        return Apotheosis.enableEnch ? Ench.Enchantments.ENDLESS_QUIVER.isTrulyInfinite(instance, stack) || original.call(instance, item) : original.call(instance, item);
    }
}
