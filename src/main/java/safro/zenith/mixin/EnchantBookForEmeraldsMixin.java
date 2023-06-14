package safro.zenith.mixin;

import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import safro.zenith.Zenith;
import safro.zenith.ench.asm.EnchHooks;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(targets = {"net/minecraft/world/entity/npc/VillagerTrades$EnchantBookForEmeralds"})
public class EnchantBookForEmeraldsMixin {

    @Redirect(method = "getOffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isTreasureOnly()Z"))
    public boolean zenithIsTreasure(Enchantment instance) {
        if (Zenith.enableEnch) {
            return EnchHooks.isTreasureOnly(instance);
        }
        return instance.isTreasureOnly();
    }

    @ModifyVariable(method = "getOffer", at = @At(value = "STORE"), ordinal = 0)
    private List zenithIsTradable(List value) {
        if (Zenith.enableEnch) {
            return Registry.ENCHANTMENT.stream().filter(EnchHooks::isTradeable).collect(Collectors.toList());
        }
        return value;
    }
}
