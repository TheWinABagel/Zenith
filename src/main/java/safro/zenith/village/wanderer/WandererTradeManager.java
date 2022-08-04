package safro.zenith.village.wanderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import safro.zenith.Zenith;
import safro.zenith.api.BasicItemListing;
import safro.zenith.api.json.ApothJsonReloadListener;
import safro.zenith.api.json.ItemAdapter;
import safro.zenith.api.json.SerializerBuilder;
import safro.zenith.village.VillageModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WandererTradeManager extends ApothJsonReloadListener<JsonTrade> {

	public static final WandererTradeManager INSTANCE = new WandererTradeManager();

	protected final Map<ResourceLocation, BasicItemListing> registry = new HashMap<>();
	protected final List<ItemListing> normTrades = new ArrayList<>();
	protected final List<ItemListing> rareTrades = new ArrayList<>();

	public WandererTradeManager() {
		super(VillageModule.LOGGER, "wanderer_trades", false, true);
	}

	@Override
	protected void registerBuiltinSerializers() {
		this.registerSerializer(new ResourceLocation(Zenith.MODID, "basic_trade"), new SerializerBuilder<JsonTrade>("Basic JSON Trade").withJsonDeserializer(obj -> {
			ItemStack price1 = ItemAdapter.ITEM_READER.fromJson(obj.get("input_1"), ItemStack.class);
			ItemStack price2 = obj.has("input_2") ? ItemAdapter.ITEM_READER.fromJson(obj.get("input_2"), ItemStack.class) : ItemStack.EMPTY;
			ItemStack output = ItemAdapter.ITEM_READER.fromJson(obj.get("output"), ItemStack.class);
			int maxTrades = GsonHelper.getAsInt(obj, "max_trades", 1);
			int xp = GsonHelper.getAsInt(obj, "xp", 0);
			float priceMult = GsonHelper.getAsFloat(obj, "price_mult", 1);
			boolean rare = GsonHelper.getAsBoolean(obj, "rare", false);
			return new BasicJsonTrade(price1, price2, output, maxTrades, xp, priceMult, rare);
		}));
	}

	@Override
	protected <T extends JsonTrade> void register(ResourceLocation key, T trade) {
		MerchantOffer offer = trade.getOffer(null, null);
		if (offer.getResult() == null || offer.getResult().isEmpty() || offer.getMaxUses() == 0) throw new RuntimeException("Wanderer Trade " + key + " is invalid.");
		super.register(key, trade);
	}

	@Override
	protected void onReload() {
		super.onReload();
		this.getValues().forEach(trade -> {
			if (trade.isRare()) this.rareTrades.add(trade);
			else this.normTrades.add(trade);
		});
	}

	public List<ItemListing> getNormalTrades() {
		return this.normTrades;
	}

	public List<ItemListing> getRareTrades() {
		return this.rareTrades;
	}

}
