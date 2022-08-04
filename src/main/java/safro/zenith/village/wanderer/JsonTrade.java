package safro.zenith.village.wanderer;

import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import safro.zenith.api.json.ApothJsonReloadListener;

public interface JsonTrade extends ItemListing, ApothJsonReloadListener.TypeKeyed<JsonTrade> {

	boolean isRare();

}
