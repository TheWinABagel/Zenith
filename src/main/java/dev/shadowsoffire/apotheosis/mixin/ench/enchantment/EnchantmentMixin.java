package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Enchantment.class, priority = 1500)
public abstract class EnchantmentMixin {

    /**
     * Adjusts the color of the enchantment text if above the vanilla max.
     *
     * @return Component with potentially modified color
     */
    @ModifyReturnValue(method = "getFullname", at = @At("RETURN"))
    public Component zenith$modifyEnchColorForAboveMaxLevel(Component original, int level) {
        if (original instanceof MutableComponent mc) {
            return ApothMiscUtil.modifyEnchantColor(level, (Enchantment) (Object) this, mc);
        }
        return original;
    }
}
