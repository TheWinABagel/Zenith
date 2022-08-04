package safro.zenith.api.enchant;

import net.minecraft.world.item.ItemStack;

public interface TableApplicableEnchant {
    boolean canApplyAtEnchantingTable(ItemStack stack);

    default boolean isAllowedOnBooks() {
        return true;
    }
}
