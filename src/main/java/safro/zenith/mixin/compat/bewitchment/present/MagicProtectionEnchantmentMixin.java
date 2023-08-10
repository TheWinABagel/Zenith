package safro.zenith.mixin.compat.bewitchment.present;

import moriyashiine.bewitchment.common.enchantment.MagicProtectionEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import safro.zenith.ench.EnchModule;

/**
 * Fixes combining tools in an anvil reducing enchantment level to it's max level when the
 * level of the enchantment is higher than it's max level. For example, combining level five
 * efficiency pickaxe with a level six efficiency pickaxe (which is higher than the max) will
 * keep the enchantment at level six instead of changing to it's max level of five.
 * <p>
 * Slightly edited to fix higher level enchantment anvil combining
 * Author: MrCrayfish
 */
@Pseudo
@Mixin(value = MagicProtectionEnchantment.class)
public class MagicProtectionEnchantmentMixin extends ProtectionEnchantment
{
    public MagicProtectionEnchantmentMixin(Rarity rarity, Type type, EquipmentSlot... equipmentSlots) {
        super(rarity, type, equipmentSlots);
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        if (ench instanceof ProtectionEnchantment pEnch) {
            return pEnch.type == Type.ALL || pEnch.type == Type.FALL;
        }
        return super.checkCompatibility(ench);
    }
}