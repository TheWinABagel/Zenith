package safro.zenith.ench.objects;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class GlowyBlock extends BlockItem {

	public GlowyBlock(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return true;
	}

}