package safro.apotheosis.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import safro.apotheosis.Apotheosis;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item {

    public ShearsItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getEnchantmentValue() {
        return Apotheosis.enableEnch ? 15 : 0;
    }
}
