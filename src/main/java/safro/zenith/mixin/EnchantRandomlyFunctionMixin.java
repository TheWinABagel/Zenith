package safro.zenith.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import safro.zenith.Zenith;
import safro.zenith.ench.asm.EnchHooks;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @Redirect(method = "enchantItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private static int zenithGetMaxLevel(Enchantment instance) {
        if (Zenith.enableEnch) {
            return EnchHooks.getMaxLootLevel(instance);
        }
        return instance.getMaxLevel();
    }
}
