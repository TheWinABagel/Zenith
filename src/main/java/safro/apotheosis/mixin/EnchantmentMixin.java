package safro.apotheosis.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import safro.apotheosis.api.enchant.TableApplicableEnchant;
import safro.apotheosis.util.ApotheosisUtil;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements TableApplicableEnchant {

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return ApotheosisUtil.canApplyItem((Enchantment) (Object) this, stack);
    }
}
