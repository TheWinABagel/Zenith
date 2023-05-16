package safro.zenith.ench.objects;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GlowyItem extends Item {

    public GlowyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

}