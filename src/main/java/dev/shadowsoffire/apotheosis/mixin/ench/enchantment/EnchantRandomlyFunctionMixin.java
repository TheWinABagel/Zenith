package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = EnchantRandomlyFunction.class, priority = 1100)// Patches vanilla loot to include possibility of config set level enchants
public abstract class EnchantRandomlyFunctionMixin {

    @WrapOperation(method = "enchantItem", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private static int redirectLootLevel(Enchantment instance, Operation<Integer> original) {
        if (!Apotheosis.enableEnch) return original.call(instance);
        return EnchHooks.getMaxLootLevel(instance);
    }

    @ModifyVariable(method = "run",
            slice = @Slice(from = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.is (Lnet/minecraft/world/item/Item;)Z", shift = At.Shift.AFTER)),
            at = @At(value = "INVOKE_ASSIGN", target = "java/util/List.isEmpty ()Z"), index = 6) // blursed
    private List<Enchantment> redirectIsDiscoverable(List<Enchantment> original, ItemStack stack, LootContext context) {
        if (Apotheosis.enableEnch) {
            if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Changing if enchantment is discoverable on {}", stack);
            return BuiltInRegistries.ENCHANTMENT.stream().filter(EnchHooks::isLootable).filter(enchantment -> stack.is(Items.BOOK) || enchantment.canEnchant(stack)).collect(Collectors.toList());
        }
        return original;
    }

}
