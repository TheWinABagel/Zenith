package safro.apotheosis;

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
import safro.apotheosis.advancements.AdvancementTriggers;
import safro.apotheosis.api.NBTIngredientPublic;
import safro.apotheosis.api.RunnableReloader;
import safro.apotheosis.api.config.Configuration;
import safro.apotheosis.api.data.RecipeHelper;
import safro.apotheosis.api.event.ServerEvents;
import safro.apotheosis.compat.PatchouliCompat;
import safro.apotheosis.deadly.DeadlyModule;
import safro.apotheosis.ench.EnchModule;
import safro.apotheosis.garden.GardenModule;
import safro.apotheosis.network.NetworkUtil;
import safro.apotheosis.potion.PotionModule;
import safro.apotheosis.spawn.SpawnerModule;
import safro.apotheosis.util.ApotheosisUtil;
import safro.apotheosis.util.ModuleCondition;
import safro.apotheosis.village.VillageModule;

import java.io.File;

public class Apotheosis implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("apotheosis");
	public static final String MODID = "apotheosis";
	public static final RecipeHelper HELPER = new RecipeHelper(Apotheosis.MODID);
	public static final CreativeModeTab APOTH_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(MODID, MODID), () -> new ItemStack(Items.ENCHANTING_TABLE));

	public static File configDir;
	public static Configuration config;
	public static boolean enableSpawner = true;
	public static boolean enableGarden = true;
	public static boolean enableDeadly = false;
	public static boolean enableEnch = true;
	public static boolean enablePotion = true;
	public static boolean enableVillage = true;

	public static float localAtkStrength = 1;

	static {
		configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), MODID);
		config = new Configuration(new File(configDir, MODID + ".cfg"));
		enableEnch = config.getBoolean("Enable Enchantment Module", "general", true, "If the enchantment module is enabled.");
		enableSpawner = config.getBoolean("Enable Spawner Module", "general", true, "If the spawner module is enabled.");
		enableGarden = config.getBoolean("Enable Garden Module", "general", true, "If the garden module is loaded.");
		//enableDeadly = config.getBoolean("Enable Deadly Module", "general", true, "If the deadly module is loaded.");
		enablePotion = config.getBoolean("Enable Potion Module", "general", true, "If the potion module is loaded.");
		enableVillage = config.getBoolean("Enable Village Module", "general", true, "If the village module is loaded.");
		config.setTitle("Apotheosis Module Control");
		config.setComment("This file allows individual modules of Apotheosis to be enabled or disabled.\nChanges will have no effect until the next game restart.");
		if (config.hasChanged()) config.save();
	}

	@Override
	public void onInitialize() {
		ServerEvents.init();
		Apoth.init();
		AdvancementTriggers.init();
		NetworkUtil.initServer();

		ResourceConditions.register(ModuleCondition.ID, ModuleCondition::test);

		if (enableEnch) EnchModule.init();
		if (enableSpawner) SpawnerModule.init();
		if (enableGarden) GardenModule.init();
		if (enableDeadly) DeadlyModule.init();
		if (enablePotion) PotionModule.init();
		if (enableVillage) VillageModule.init();

		if (config.hasChanged()) config.save();

		ApotheosisUtil.registerTypes();

		addReloads();

		if (FabricLoader.getInstance().isModLoaded("patchouli")) PatchouliCompat.register();
	}

	public static Ingredient potionIngredient(Potion type) {
		return new NBTIngredientPublic(PotionUtils.setPotion(new ItemStack(Items.POTION), type));
	}

	public static void addReloads() {
		RunnableReloader.add(() -> {
			if (enableDeadly) DeadlyModule.reload(true);
		}, "deadly_module");
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
