package safro.apotheosis.api.enchant;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public interface TableApplicableItem {
    default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.is(ItemTags.ANVIL)) return stack.getCount() == 1 && (enchantment == Enchantments.UNBREAKING || enchantment.canEnchant(stack));
        return enchantment.canEnchant(stack);
    }
}
