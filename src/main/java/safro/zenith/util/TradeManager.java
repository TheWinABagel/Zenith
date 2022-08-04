package safro.zenith.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerTrades;
import safro.zenith.village.wanderer.WandererReplacements;

import java.util.Arrays;
import java.util.List;

public class TradeManager {
    private static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERER_TRADES = new Int2ObjectOpenHashMap<>();

    static {
        VillagerTrades.WANDERING_TRADER_TRADES.int2ObjectEntrySet().forEach(e -> WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf(e.getValue(), e.getValue().length)));
    }

    public static void postWanderer() {
        List<VillagerTrades.ItemListing> generic = NonNullList.create();
        List<VillagerTrades.ItemListing> rare = NonNullList.create();
        Arrays.stream(WANDERER_TRADES.get(1)).forEach(generic::add);
        Arrays.stream(WANDERER_TRADES.get(2)).forEach(rare::add);
        WandererReplacements.replaceWandererArrays(generic, rare);
        VillagerTrades.WANDERING_TRADER_TRADES.put(1, generic.toArray(new VillagerTrades.ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, rare.toArray(new VillagerTrades.ItemListing[0]));
    }
}
