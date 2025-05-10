package dev.shadowsoffire.apotheosis.ench;

import dev.shadowsoffire.placebo.color.GradientColor;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.network.chat.TextColor;

import java.util.Locale;

public class EnchConfig {

    public static boolean showEnchantedBookMetadata = true;
    public static int sculkShelfNoiseChance = 200;
    public static TextColor overleveledEnchColor = Ench.Colors.LIGHT_BLUE_FLASH;

    private static final String COLOR_COMMENT = "The color used for enchantments higher than their normal level. Can either be a formatting code (https://minecraft.wiki/w/Formatting_codes#Color_codes), a hex color value starting with #, or RAINBOW. Set to blank to disable.";

    public static void load(Configuration c) {
        c.setTitle("Zenith Enchantment Module Config");

        showEnchantedBookMetadata = c.getBoolean("Show Enchanted Book Metadata", "client", true, "If enchanted book metadata (treasure, tradeable, etc) are shown in the tooltip.");
        sculkShelfNoiseChance = c.getInt("Sculkshelf Noise Chance", "client", 200, 0, 32767, "The 1/n chance that a sculkshelf plays a sound, per client tick. Set to 0 to disable.");
        overleveledEnchColor = getOverlevelEnchColor(c.getString("Over-Leveled Enchantment Color", "client", Ench.Colors.LIGHT_BLUE_FLASH.toString().toUpperCase(Locale.ROOT), COLOR_COMMENT));
        if (c.hasChanged()) c.save();
    }

    public static TextColor getOverlevelEnchColor(String string) {
        if (string == null || string.isEmpty()) return null; //disabled
        if (string.equalsIgnoreCase("rainbow")) return GradientColor.RAINBOW;
        if (string.equalsIgnoreCase("light_blue_flash")) return Ench.Colors.LIGHT_BLUE_FLASH;
        TextColor color = TextColor.parseColor(string);
        if (color == null) {
            EnchModule.LOGGER.warn("Failed to parse color from string {}! Setting to LIGHT_BLUE", string);
            return Ench.Colors.LIGHT_BLUE_FLASH;
        }
        else
            return color;
    }

}