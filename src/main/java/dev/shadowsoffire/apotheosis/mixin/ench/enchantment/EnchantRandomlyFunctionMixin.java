package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = EnchantRandomlyFunction.class, priority = 1100)// Patches vanilla loot to include possibility of config set level enchants
public class EnchantRandomlyFunctionMixin {


    @Redirect(method = "enchantItem", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private static int redirectLootLevel(Enchantment instance){
        if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Changing possible max level of enchantment {}", instance);
        if (!Apotheosis.enableEnch) return instance.getMaxLevel();
        return EnchHooks.getMaxLootLevel(instance);
    }

    @Shadow
    private static ItemStack enchantItem(ItemStack stack, Enchantment enchantment, RandomSource random) {
        return null;
    }
/*
    @Inject(method = "run", at = @At(value = "INVOKE", target = "java/util/List.isEmpty ()Z", ordinal = 1, shift = At.Shift.BEFORE))
    private void whee(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {

    }*/


    @ModifyVariable(method = "run",
            slice = @Slice(from = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.is (Lnet/minecraft/world/item/Item;)Z", shift = At.Shift.AFTER)),
            at = @At(value = "INVOKE_ASSIGN", target = "java/util/List.isEmpty ()Z"), index = 6) // blursed
    private List redirectIsDiscoverable(List original, ItemStack stack, LootContext context) {
        if (Apotheosis.enableEnch) {
            if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Changing if enchantment is discoverable on {}", stack);
            return BuiltInRegistries.ENCHANTMENT.stream().filter(EnchHooks::isLootable).filter(enchantment -> stack.is(Items.BOOK) || enchantment.canEnchant(stack)).collect(Collectors.toList());
        }
        return original;
    }



}
