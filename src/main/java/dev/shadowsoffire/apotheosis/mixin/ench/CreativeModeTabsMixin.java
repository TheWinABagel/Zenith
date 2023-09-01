package dev.shadowsoffire.apotheosis.mixin.ench;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.stream.IntStream;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin { // should be wrap operation, will fix if issues come up

    @Inject(method = "generateEnchantmentBookTypesOnlyMaxLevel", at = @At("HEAD"), cancellable = true)
    private static void ZenithChangeCreativeMaxBooks(CreativeModeTab.Output output, HolderLookup<Enchantment> enchantments, Set<EnchantmentCategory> categories, CreativeModeTab.TabVisibility tabVisibility, CallbackInfo ci) {
        if (Apotheosis.enableEnch) {
            enchantments.listElements().map(Holder::value).filter(enchantment -> categories.contains((Object) enchantment.category)).map(enchantment -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance((Enchantment) enchantment, EnchHooks.getMaxLevel(enchantment)))).forEach(itemStack -> output.accept((ItemStack) itemStack, tabVisibility));
            ci.cancel();
        }
    }

    @Inject(method = "generateEnchantmentBookTypesAllLevels", at = @At("HEAD"), cancellable = true)
    private static void ZenithChangeCreativeAllBooks(CreativeModeTab.Output output, HolderLookup<Enchantment> enchantments, Set<EnchantmentCategory> categories, CreativeModeTab.TabVisibility tabVisibility, CallbackInfo ci) {
        if (Apotheosis.enableEnch) {
            enchantments.listElements().map(Holder::value).filter(enchantment -> categories.contains((Object)enchantment.category)).flatMap(enchantment -> IntStream.rangeClosed( enchantment.getMinLevel(), EnchHooks.getMaxLevel(enchantment)).mapToObj(i -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance((Enchantment)enchantment, i)))).forEach(itemStack -> output.accept((ItemStack)itemStack, tabVisibility));
            ci.cancel();
        }
    }

}