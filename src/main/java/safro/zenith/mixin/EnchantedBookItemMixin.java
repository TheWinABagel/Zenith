package safro.zenith.mixin;

import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import safro.zenith.Zenith;
import safro.zenith.ench.asm.EnchHooks;

@Mixin(value = EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @Redirect(method = "fillItemCategory", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private int zenithModifyMaxLevel(Enchantment enchantment) {
        if (!Zenith.enableEnch)
            return enchantment.getMaxLevel();
        return EnchHooks.getMaxLevel(enchantment);
    }
}
