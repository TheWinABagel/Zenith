package safro.apotheosis.deadly.config;

import safro.apotheosis.api.config.Configuration;
import safro.apotheosis.deadly.loot.LootRarity;

import java.util.Locale;

public class DeadlyConfig {
    public static int[] rarityThresholds = new int[] { 400, 700, 880, 950, 995 };

    public static void load(Configuration c) {
        int i = 0;
        for (LootRarity r : LootRarity.values()) {
            if (r != LootRarity.ANCIENT) {
                int threshold = c.getInt(r.name().toLowerCase(Locale.ROOT), "rarity", rarityThresholds[i], 0, 1000, "The threshold for this rarity.  The percentage chance of this rarity appearing is equal to (previous threshold - this threshold) / 10.");
                rarityThresholds[i++] = threshold;
            }
        }
    }
}
