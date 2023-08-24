package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import blue.endless.jankson.JsonArray;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = EnchantRandomlyFunction.class, priority = 1100)// Patches vanilla loot to include possiblity of config set level enchants
public class EnchantRandomlyFunctionMixin {
    @Final
    @Shadow
    List<Enchantment> enchantments;

    @Final
    @Shadow
    private static Logger LOGGER;

    @Redirect(method = "enchantItem", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private static int redirectLootLevel(Enchantment instance){
        if (!Apotheosis.enableEnch) return instance.getMaxLevel();
        return EnchHooks.getMaxLootLevel(instance);
    }

    @Shadow
    private static ItemStack enchantItem(ItemStack stack, Enchantment enchantment, RandomSource random) {
        return null;
    }

    //@ModifyVariable(method = "run", at = @At(value = "STORE"), ordinal = 2)
    @Inject(method = "run", at = @At("HEAD"), cancellable = true) // this sucks and is terrible and I hate it, why wont modifyvariable work?
    private void redirectIsDiscoverable(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (Apotheosis.enableEnch){
            Enchantment enchantment2;
            RandomSource randomSource = context.getRandom();
            if (this.enchantments.isEmpty()) {
                boolean bl = stack.is(Items.BOOK);
                List list = BuiltInRegistries.ENCHANTMENT.stream().filter(EnchHooks::isLootable).filter(enchantment -> bl || enchantment.canEnchant(stack)).collect(Collectors.toList());
                if (list.isEmpty()) {
                    LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object) stack);
                    cir.setReturnValue(stack);
                }
                enchantment2 = (Enchantment) list.get(randomSource.nextInt(list.size()));
            } else {
                enchantment2 = this.enchantments.get(randomSource.nextInt(this.enchantments.size()));
            }
            cir.setReturnValue(this.enchantItem(stack, enchantment2, randomSource));
        }
    }



}
