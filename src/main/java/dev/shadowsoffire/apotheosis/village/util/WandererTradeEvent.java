package dev.shadowsoffire.apotheosis.village.util;

import dev.shadowsoffire.apotheosis.village.wanderer.WandererReplacements;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.Arrays;
import java.util.List;

public class WandererTradeEvent {
    private static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERER_TRADES = new Int2ObjectOpenHashMap<>();

    static {
        VillagerTrades.WANDERING_TRADER_TRADES.int2ObjectEntrySet().forEach(e -> WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf(e.getValue(), e.getValue().length)));
    }

    public static void postWandererEvent()
    {
        List<VillagerTrades.ItemListing> generic = NonNullList.create();
        List<VillagerTrades.ItemListing> rare = NonNullList.create();
        generic.addAll(Arrays.asList(WANDERER_TRADES.get(1)));
        rare.addAll(Arrays.asList(WANDERER_TRADES.get(2)));
        WandererReplacements.replaceWandererArrays(generic, rare);
        VillagerTrades.WANDERING_TRADER_TRADES.put(1, generic.toArray(new VillagerTrades.ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, rare.toArray(new VillagerTrades.ItemListing[0]));
    }
}
