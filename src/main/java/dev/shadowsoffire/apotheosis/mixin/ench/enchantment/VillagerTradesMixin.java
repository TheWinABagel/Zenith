package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public abstract class VillagerTradesMixin {

    @WrapOperation(method = "getOffer", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.isTreasureOnly ()Z"))
    private boolean zenith_redirectIsTradable(Enchantment ench, Operation<Boolean> original) {
        return EnchHooks.isTreasureOnly(ench);
    }

    @ModifyVariable(method = "getOffer", at = @At(value = "STORE"), ordinal = 0)
    private List zenith_modifyIsTradable(List value) {
        if (!Apotheosis.enableEnch) return value;
        return BuiltInRegistries.ENCHANTMENT.stream().filter(EnchHooks::isTradeable).collect(Collectors.toList());
    }

    @WrapOperation(method = "getOffer", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private int zenith_redirectLootableLevel(Enchantment ench, Operation<Integer> original) {
        if (!Apotheosis.enableEnch) return original.call(ench);
        return EnchHooks.getMaxLootLevel(ench);
    }
}
