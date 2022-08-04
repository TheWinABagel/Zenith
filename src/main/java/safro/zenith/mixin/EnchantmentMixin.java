package safro.zenith.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import safro.zenith.api.enchant.TableApplicableEnchant;
import safro.zenith.util.ApotheosisUtil;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements TableApplicableEnchant {

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return ApotheosisUtil.canApplyItem((Enchantment) (Object) this, stack);
    }
}
