package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
public abstract class DamageEnchantmentMixin extends Enchantment {

    public DamageEnchantmentMixin(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }

    @Inject(method = "checkCompatibility(Lnet/minecraft/world/item/enchantment/Enchantment;)Z", at = @At("RETURN"), cancellable = true)
    protected void checkCompatibility(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (Apotheosis.enableEnch){
            DamageEnchantment ths = ((DamageEnchantment) (Object) this);
            if (ths.type == 0){
                cir.setReturnValue(other != ths);
                return;
            }
            cir.setReturnValue(other == Enchantments.SHARPNESS ? other != ths : !(other instanceof DamageEnchantment));
        }
    }
}
