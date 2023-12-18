package dev.shadowsoffire.apotheosis.village.wanderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.shadowsoffire.apotheosis.village.util.BasicItemListing;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.json.ItemAdapter;
import dev.shadowsoffire.placebo.json.NBTAdapter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The wandering merchant sucks. Trades are totally underwhelming and are borderline garbage 99% of the time.
 * The village-module-specific trades are only enabled if the module is enabled, but this data loader is always enabled.
 *
 * @author Shadows
 */
public class WandererReplacements {

    public static boolean clearNormTrades = false;
    public static boolean clearRareTrades = false;
    public static boolean undergroundTrader = true;

    public static int wandererMaxChance = 101;
    public static int wandererRngCoeff = 2;
    public static int despawnDelay = 24000;

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(BasicItemListing.class, BasicItemListingAdapter.INSTANCE).registerTypeAdapter(ItemStack.class, ItemAdapter.INSTANCE)
        .registerTypeAdapter(CompoundTag.class, NBTAdapter.INSTANCE).create();

    public static void setup() {
        WandererTradesRegistry.INSTANCE.register();
    }

    public static void replaceWandererArrays(List<VillagerTrades.ItemListing> generic,List<VillagerTrades.ItemListing> rare) {
        if (clearNormTrades) generic.clear();
        if (clearRareTrades) rare.clear();
        generic.addAll(WandererTradesRegistry.INSTANCE.getNormalTrades());
        rare.addAll(WandererTradesRegistry.INSTANCE.getRareTrades());
    }

    public static void load(Configuration cfg) {
        clearNormTrades = cfg.getBoolean("Clear Generic Trades", "wanderer", clearNormTrades, "If the generic trade list will be cleared before datapack loaded trades are added.\nServer-authoritative.");
        clearRareTrades = cfg.getBoolean("Clear Rare Trades", "wanderer", clearRareTrades, "If the rare trade list will be cleared before datapack loaded trades are added.\nServer-authoritative.");
        undergroundTrader = cfg.getBoolean("Underground Trader", "wanderer", undergroundTrader, "If the Wandering Trader can attempt to spawn underground.\nServer-authoritative.");
    }
}
