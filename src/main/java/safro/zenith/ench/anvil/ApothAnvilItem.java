package safro.zenith.ench.anvil;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import safro.zenith.api.enchant.TableApplicableItem;

public class ApothAnvilItem extends BlockItem implements TableApplicableItem {

	public ApothAnvilItem(Block block) {
		super(block, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
	}

	@Override
	public int getEnchantmentValue() {
		return 50;
	}

}