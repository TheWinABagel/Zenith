package safro.zenith.village.wanderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import safro.zenith.api.BasicItemListing;
import safro.zenith.api.config.Configuration;
import safro.zenith.api.json.ItemAdapter;
import safro.zenith.api.json.NBTAdapter;

import java.util.List;

/**
 * The wandering merchant sucks.  Trades are totally underwhelming and are borderline garbage 99% of the time.
 * @author Shadows
 *
 */
public class WandererReplacements {

	public static boolean clearNormTrades = false;
	public static boolean clearRareTrades = false;
	public static boolean affixTrades = true;

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(BasicItemListing.class, BasicItemListingAdapter.INSTANCE).registerTypeAdapter(ItemStack.class, ItemAdapter.INSTANCE).registerTypeAdapter(CompoundTag.class, NBTAdapter.INSTANCE).create();


	public static void replaceWandererArrays(List<VillagerTrades.ItemListing> generic, List<VillagerTrades.ItemListing> rare) {
		if (clearNormTrades) generic.clear();
		if (clearRareTrades) rare.clear();
		generic.addAll(WandererTradeManager.INSTANCE.getNormalTrades());
		generic.addAll(WandererTradeManager.INSTANCE.getRareTrades());
	}

	public static void load(Configuration cfg) {
		clearNormTrades = cfg.getBoolean("Clear Generic Trades", "wanderer", false, "If the generic trade list will be cleared before datapack loaded trades are added.");
		clearRareTrades = cfg.getBoolean("Clear Rare Trades", "wanderer", false, "If the rare trade list will be cleared before datapack loaded trades are added.");
	}
}