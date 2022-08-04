package safro.zenith.mixin;

import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.asm.EnchHooks;

@Mixin(CombatRules.class)
public class CombatRulesMixin {

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private static void getDamageAfterMagicAbsorb(float damage, float enchantModifiers, CallbackInfoReturnable<Float> ci) {
        if (Zenith.enableEnch) ci.setReturnValue(EnchHooks.getDamageAfterMagicAbsorb(damage, enchantModifiers));
    }
}
