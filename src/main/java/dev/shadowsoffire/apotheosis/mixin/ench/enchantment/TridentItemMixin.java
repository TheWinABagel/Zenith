package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends Item implements CustomEnchantingBehaviorItem {

    public TridentItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {
        if (!Apotheosis.enableEnch) return ench.category.canEnchant(stack.getItem());
        return ench.category.canEnchant(stack.getItem()) || ench == Enchantments.SHARPNESS || ench == Enchantments.MOB_LOOTING || ench == Enchantments.PIERCING;
    }
}
