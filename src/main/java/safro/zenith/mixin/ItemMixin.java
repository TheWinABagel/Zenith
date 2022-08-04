package safro.zenith.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import safro.zenith.Zenith;
import safro.zenith.api.enchant.TableApplicableItem;
import safro.zenith.ench.table.IEnchantableItem;

@Mixin(Item.class)
public class ItemMixin implements TableApplicableItem, IEnchantableItem {
    /**
     * @author Shadows
     * @reason Enables all items to be enchantable by default.
     * @return
     */
    @Overwrite
    public int getEnchantmentValue() {
        return Zenith.enableEnch ? 1 : 0;
    }
}
