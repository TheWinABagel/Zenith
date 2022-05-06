package safro.apotheosis.village.wanderer;

import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import safro.apotheosis.api.json.ApothJsonReloadListener;

public interface JsonTrade extends ItemListing, ApothJsonReloadListener.TypeKeyed<JsonTrade> {

	boolean isRare();

}
