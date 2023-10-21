package dev.shadowsoffire.apotheosis;

import dev.shadowsoffire.apotheosis.advancements.AdvancementTriggers;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.compat.PatchouliCompat;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModule;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.apotheosis.util.ModuleCondition;
import dev.shadowsoffire.apotheosis.village.VillageModule;
import dev.shadowsoffire.placebo.config.Configuration;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.io.File;

public class Apotheosis implements ModInitializer {

    public static final String MODID = "zenith";

    public static File configDir;
    public static Configuration config;
    public static boolean enableEnch = true;
    public static boolean enableAdventure = true;
    public static boolean enableSpawner = true;
    public static boolean enablePotion = true;
    public static boolean enableVillage = true;
    public static boolean enableGarden = true;
    public static boolean enableDebug = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static boolean giveBook = true;

    public static float localAtkStrength = 1;

    @Override
    public void onInitialize() {
        if (enableEnch) EnchModule.init();
        if (enableSpawner) SpawnerModule.init();
        if (enableGarden) GardenModule.init();
        if (enableAdventure) AdventureModule.init();
        if (enablePotion) PotionModule.init();
        if (enableVillage) {
            VillageModule.init();
            VillageModule.FLETCHING.getClass(); // Static init wew
        }

        if (config.hasChanged()) config.save();

        AdvancementTriggers.init();
        Events.init();
        ResourceConditions.register(ModuleCondition.ID, ModuleCondition::test);
        if (FabricLoader.getInstance().isModLoaded("patchouli")) PatchouliCompat.registerPatchouli();

        Apoth.Tiles.bootstrap();
    }

    static {
        configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), MODID);
        config = new Configuration(new File(configDir, MODID + ".cfg"));
        enableEnch = config.getBoolean("Enable Enchantment Module", "general", true, "If the enchantment module is enabled.");
        enableAdventure = config.getBoolean("Enable Adventure Module", "general", true, "If the adventure module is loaded.");
        enableSpawner = config.getBoolean("Enable Spawner Module", "general", true, "If the spawner module is enabled.");
        enablePotion = config.getBoolean("Enable Potion Module", "general", true, "If the potion module is loaded.");
        enableVillage = config.getBoolean("Enable Village Module", "general", true, "If the village module is loaded.");
        enableGarden = config.getBoolean("Enable Garden Module", "general", true, "If the garden module is loaded.");
        enableDebug = config.getBoolean("Enable Debug mode", "general", FabricLoader.getInstance().isDevelopmentEnvironment(), "If a lot of random debug info is added to the console. Not recommended for normal play.");
        giveBook = config.getBoolean("Give Book on First Join", "general", true, "If the Chronicle of Shadows is given to new players.");
        config.setTitle("Zenith Module Control");
        config.setComment("This file allows individual modules of Zenith to be enabled or disabled.\nChanges will have no effect until the next game restart.\nThis file must match on client and server.");
        config.setComment("Report any issues found to https://github.com/TheWinABagel/Zenith/issues, not to Apotheosis!");
        if (config.hasChanged()) config.save();

    }
    public static ResourceLocation loc(String id) {
        return new ResourceLocation(MODID, id);
    }

}
