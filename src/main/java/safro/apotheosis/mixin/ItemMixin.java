package safro.apotheosis.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.api.enchant.TableApplicableItem;
import safro.apotheosis.ench.table.IEnchantableItem;

@Mixin(Item.class)
public class ItemMixin implements TableApplicableItem, IEnchantableItem {
    /**
     * @author Shadows
     * @reason Enables all items to be enchantable by default.
     * @return
     */
    @Overwrite
    public int getEnchantmentValue() {
        return Apotheosis.enableEnch ? 1 : 0;
    }
}
