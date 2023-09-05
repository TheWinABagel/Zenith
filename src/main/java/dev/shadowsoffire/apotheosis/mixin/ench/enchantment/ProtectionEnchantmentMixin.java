package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Inject(method = "checkCompatibility(Lnet/minecraft/world/item/enchantment/Enchantment;)Z", at = @At("HEAD"), cancellable = true)
    protected void checkCompatibility(Enchantment ench, CallbackInfoReturnable<Boolean> cir) {
        if (Apotheosis.enableEnch){
            if (ths() == Enchantments.FALL_PROTECTION || ths() == Enchantments.ALL_DAMAGE_PROTECTION) cir.setReturnValue(ench != ths());
            if (ench instanceof ProtectionEnchantment pEnch) {
                if (ench == ths()) cir.setReturnValue(false);
                cir.setReturnValue(pEnch.type == ProtectionEnchantment.Type.ALL || pEnch.type == ProtectionEnchantment.Type.FALL);
            }
            cir.setReturnValue(ench != ths());
        }
    }

    @ModifyConstant(method = "getDamageProtection", constant = @Constant(intValue = 2, ordinal = 0))
    private int modifyFireProt(int constant){
        if (!Apotheosis.enableEnch) return constant;
        return 1;
    }

    @ModifyConstant(method = "getDamageProtection", constant = @Constant(intValue = 2, ordinal = 2))
    private int modifyProjProt(int constant){
        if (!Apotheosis.enableEnch) return constant;
        return 1;
    }

    private ProtectionEnchantment ths(){
        return (ProtectionEnchantment) (Object) this;
    }


}
