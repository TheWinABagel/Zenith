package safro.zenith.potion;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import safro.zenith.Zenith;

public class LuckyFootItem extends Item {

	public LuckyFootItem() {
		super(new Item.Properties().stacksTo(1).tab(Zenith.ZENITH_GROUP));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

}