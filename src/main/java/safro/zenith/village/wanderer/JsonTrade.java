package safro.zenith.village.wanderer;

import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import safro.zenith.api.json.ZenithJsonReloadListener;

public interface JsonTrade extends ItemListing, ZenithJsonReloadListener.TypeKeyed<JsonTrade> {

	boolean isRare();

}
