package safro.zenith.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import safro.zenith.Zenith;
import safro.zenith.api.enchant.TableApplicableItem;
import safro.zenith.ench.table.IEnchantableItem;
import safro.zenith.util.ZenithUtil;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item implements IEnchantableItem {

    public ShearsItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getEnchantmentValue() {
        return Zenith.enableEnch ? 15 : 0;
    }
}
