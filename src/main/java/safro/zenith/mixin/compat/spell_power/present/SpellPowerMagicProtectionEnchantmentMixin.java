package safro.zenith.mixin.compat.spell_power.present;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.spell_power.internals.MagicProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;

/**
 * Fixes combining tools in an anvil reducing enchantment level to it's max level when the
 * level of the enchantment is higher than it's max level. For example, combining level five
 * efficiency pickaxe with a level six efficiency pickaxe (which is higher than the max) will
 * keep the enchantment at level six instead of changing to it's max level of five.
 * <p>
 * Slightly edited to fix higher level enchantment anvil combining
 * Author: MrCrayfish
 */
@Mixin(value = MagicProtectionEnchantment.class)
public class SpellPowerMagicProtectionEnchantmentMixin extends ProtectionEnchantment
{
    public SpellPowerMagicProtectionEnchantmentMixin(Rarity rarity, Type type, EquipmentSlot... equipmentSlots) {
        super(rarity, type, equipmentSlots);
    }

@Override
    public boolean checkCompatibility(Enchantment ench) {
    if (Zenith.enableEnch) {
        if (ench instanceof ProtectionEnchantment pEnch) {
            return (pEnch.type == Type.ALL || pEnch.type == Type.FALL);
        }
        return (super.checkCompatibility(ench));
    } else {
    if (ench instanceof ProtectionEnchantment) {
        ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment) ench;
        if (this.type == protectionEnchantment.type) {
            return false;
        }
        return this.type == Type.FALL || protectionEnchantment.type == Type.FALL;
    }
    return super.checkCompatibility(ench);
    }
}

}