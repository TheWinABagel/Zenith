package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class VillagerTradesMixin {

    @Redirect(method = "getOffer", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.isTreasureOnly ()Z"))
    private boolean zenith_redirectIsTradable(Enchantment ench) {
        return EnchHooks.isTreasureOnly(ench);
    }

    @ModifyVariable(method = "getOffer", at = @At(value = "STORE"), ordinal = 0)
    private List zenith_modifyIsTradable(List value) {
        if (!Apotheosis.enableEnch) return value;
        return BuiltInRegistries.ENCHANTMENT.stream().filter(EnchHooks::isTradeable).collect(Collectors.toList());
    }

    @Redirect(method = "getOffer", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private int zenith_redirectLootableLevel(Enchantment ench) {
        return EnchHooks.getMaxLootLevel(ench);
    }

}
