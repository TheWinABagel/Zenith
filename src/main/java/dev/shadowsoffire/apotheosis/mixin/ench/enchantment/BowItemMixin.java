package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/EnchantmentHelper.getItemEnchantmentLevel (Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I", ordinal = 0))
    private int endlessQuiver(Enchantment enchantment, ItemStack stack){
        int infinityLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack);
        if (!Apotheosis.enableEnch) return infinityLevel;
        int endlessLevel = EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.ENDLESS_QUIVER, stack);
        return endlessLevel > 0 ? endlessLevel : infinityLevel;
    }

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean zenithEndlessQuiver(ItemStack stack, Item item, ItemStack bow, Level level, LivingEntity livingEntity, int i) {
        return Apotheosis.enableEnch ? Ench.Enchantments.ENDLESS_QUIVER.isTrulyInfinite(stack, bow) || stack.is(Items.ARROW) : stack.is(Items.ARROW);
    }
}
