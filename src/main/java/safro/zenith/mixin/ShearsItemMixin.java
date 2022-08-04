package safro.zenith.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import safro.zenith.Zenith;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item {

    public ShearsItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getEnchantmentValue() {
        return Zenith.enableEnch ? 15 : 0;
    }
}
