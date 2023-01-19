package safro.zenith;

import io.github.fabricators_of_create.porting_lib.crafting.NBTIngredient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import safro.zenith.advancements.AdvancementTriggers;
import safro.zenith.api.RunnableReloader;
import safro.zenith.api.config.Configuration;
import safro.zenith.api.data.RecipeHelper;
import safro.zenith.api.event.ServerEvents;
import safro.zenith.compat.PatchouliCompat;
import safro.zenith.ench.EnchModule;
import safro.zenith.garden.GardenModule;
import safro.zenith.network.NetworkUtil;
import safro.zenith.potion.PotionModule;
import safro.zenith.spawn.SpawnerModule;
import safro.zenith.util.ApotheosisUtil;
import safro.zenith.util.ModuleCondition;
import safro.zenith.village.VillageModule;

import java.io.File;

public class Zenith implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("zenith");
	public static final String MODID = "zenith";
	public static final RecipeHelper HELPER = new RecipeHelper(Zenith.MODID);
	public static final CreativeModeTab APOTH_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(MODID, MODID), () -> new ItemStack(Items.ENCHANTING_TABLE));

	public static File configDir;
	public static Configuration config;
	public static boolean enableSpawner = true;
	public static boolean enableGarden = true;
//	public static boolean enableAdventure = false;
	public static boolean enableEnch = true;
	public static boolean enablePotion = true;
	public static boolean enableVillage = true;
	public static boolean giveBook = true;

	public static float localAtkStrength = 1;

	static {
		configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), MODID);
		config = new Configuration(new File(configDir, MODID + ".cfg"));
		enableEnch = config.getBoolean("Enable Enchantment Module", "general", true, "If the enchantment module is enabled.");
		enableSpawner = config.getBoolean("Enable Spawner Module", "general", true, "If the spawner module is enabled.");
		enableGarden = config.getBoolean("Enable Garden Module", "general", true, "If the garden module is loaded.");
//		enableAdventure = config.getBoolean("Enable Adventure Module", "general", true, "If the adventure module is loaded.");
		enablePotion = config.getBoolean("Enable Potion Module", "general", true, "If the potion module is loaded.");
		enableVillage = config.getBoolean("Enable Village Module", "general", true, "If the village module is loaded.");
		giveBook = config.getBoolean("Give Book on First Join", "general", true, "If the Chronicle of Shadows is given to new players.");
		config.setTitle("Zenith Module Control");
		config.setComment("This file allows individual modules of Zenith to be enabled or disabled.\nChanges will have no effect until the next game restart.");
		if (config.hasChanged()) config.save();
	}

	@Override
	public void onInitialize() {
		ServerEvents.init();
		Apoth.init();
		AdvancementTriggers.init();
		NetworkUtil.initServer();

		ResourceConditions.register(ModuleCondition.ID, ModuleCondition::test);
		if (FabricLoader.getInstance().isModLoaded("patchouli")) PatchouliCompat.register();

		if (enableEnch) EnchModule.init();
		if (enableSpawner) SpawnerModule.init();
		if (enableGarden) GardenModule.init();
//		if (enableAdventure) AdventureModule.init();
		if (enablePotion) PotionModule.init();
		if (enableVillage) VillageModule.init();

		if (config.hasChanged()) config.save();

		ApotheosisUtil.registerTypes();

		addReloads();
	}

	public static Ingredient potionIngredient(Potion type) {
		return NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), type));
	}

	public static void addReloads() {
//		RunnableReloader.add(() -> {
//			if (enableAdventure) AdventureModule.init();
//		}, "adventure_module");
		RunnableReloader.add(() -> {
			if (enableEnch) EnchModule.reload(true);
		}, "enchantment_module");
		RunnableReloader.add(() -> {
			if (enablePotion) PotionModule.reload(true);
		}, "potion_module");
		RunnableReloader.add(() -> {
			if (enableGarden) GardenModule.reload(true);
		}, "garden_module");
		RunnableReloader.add(() -> {
			if (enableSpawner) SpawnerModule.reload(true);
		}, "spawner_module");
	}
}
