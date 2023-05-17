package safro.zenith.adventure;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import safro.zenith.Zenith;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixManager;
import safro.zenith.adventure.affix.effect.MagicalArrowAffix;
import safro.zenith.adventure.affix.socket.SocketAffix;
import safro.zenith.adventure.affix.socket.gem.GemItem;
import safro.zenith.adventure.affix.socket.gem.GemManager;
import safro.zenith.adventure.affix.socket.gem.bonus.GemBonus;
import safro.zenith.api.config.Configuration;
import safro.zenith.api.data.LootSystem;
import safro.zenith.api.placebo.json.DynamicRegistryObject;
import safro.zenith.ench.objects.GlowyItem;
import safro.zenith.util.NameHelper;

import java.io.File;

public class AdventureModule {

	public static final Logger LOGGER = LogManager.getLogger("Zenith : Adventure");

	public static final Registry<Affix> AFFIXES = FabricRegistryBuilder.createSimple(Affix.class, new ResourceLocation(Zenith.MODID, "affixes")).buildAndRegister();

	//public static final BiMap<LootRarity, Item> RARITY_MATERIALS = HashBiMap.create();
	//public static final RecipeSerializer<SocketingRecipe> SOCKETING_SERIALIZER = new SocketingRecipe.Serializer();
	//public static final RecipeSerializer<ExpulsionRecipe> EXPULSION_SERIALIZER = new ExpulsionRecipe.Serializer();
	//public static final RecipeSerializer<ExtractionRecipe> EXTRACTION_SERIALIZER = new ExtractionRecipe.Serializer();

	public static final boolean STAGES_LOADED = FabricLoader.getInstance().isModLoaded("gamestages");

	//TODO redo as mixin
	 //	ObfuscationReflectionHelper.setPrivateValue(RangedAttribute.class, (RangedAttribute) Attributes.ARMOR, 40D, "f_22308_");
	 //	ObfuscationReflectionHelper.setPrivateValue(RangedAttribute.class, (RangedAttribute) Attributes.ARMOR_TOUGHNESS, 30D, "f_22308_");

	//Attributes
	/**
	 * Bonus to how fast a ranged weapon is charged. Base Value = (1.0) = 100%
	 */
	public static final Attribute DRAW_SPEED = register("draw_speed", new RangedAttribute("zenith:draw_speed", 1.0D, 1.0D, 4.0D).setSyncable(true));
	/**
	 * Chance that a non-jump-attack will critically strike.  Base value = (1.0) = 0%
	 */
	public static final Attribute CRIT_CHANCE = register("crit_chance", new RangedAttribute("zenith:crit_chance", 1.5D, 1.0D, 1024.0D).setSyncable(true));
	/**
	 * Amount of damage caused by critical strikes. Base value = (1.0) = 100%
	 * Not related to vanilla critical strikes.
	 */
	public static final Attribute CRIT_DAMAGE = register("crit_damage", new RangedAttribute("zenith:crit_damage", 1.0D, 1.0D, 1024.0D).setSyncable(true));
	/**
	 * Bonus magic damage that slows enemies hit. Base value = (0.0) = 0 damage
	 */
	public static final Attribute COLD_DAMAGE = register("cold_damage", new RangedAttribute("zenith:cold_damage", 0.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Bonus magic damage that burns enemies hit. Base value = (0.0) = 0 damage
	 */
	public static final Attribute FIRE_DAMAGE = register("fire_damage", new RangedAttribute("zenith:fire_damage", 0.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Percent of physical damage converted to health. Base value = (1.0) = 0%
	 */
	public static final Attribute LIFE_STEAL = register("life_steal", new RangedAttribute("zenith:life_steal", 1.0D, 1.0D, 1024.0D).setSyncable(true));
	/**
	 * Percent of physical damage that bypasses armor. Base value = (1.0) = 0%
	 */
	public static final Attribute PIERCING = register("piercing", new RangedAttribute("zenith:piercing", 1.0D, 1.0D, 2.0D).setSyncable(true));
	/**
	 * Bonus physical damage dealt equal to enemy's current health. Base value = (1.0) = 0%
	 */
	public static final Attribute CURRENT_HP_DAMAGE = register("current_hp_damage", new RangedAttribute("zenith:current_hp_damage", 1.0D, 1.0D, 2.0D).setSyncable(true));
	/**
	 * Percent of physical damage converted to absorption hearts. Base value = (1.0) = 0%
	 */
	public static final Attribute OVERHEAL = register("overheal", new RangedAttribute("zenith:overheal", 1.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Extra health that regenerates when not taking damage. Base value = (0.0) = 0 damage
	 */
	public static final Attribute GHOST_HEALTH = register("ghost_health", new RangedAttribute("zenith:ghost_health", 0.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Mining Speed. Base value = (1.0) = 100% default break speed
	 */
	public static final Attribute MINING_SPEED = register("mining_speed", new RangedAttribute("zenith:mining_speed", 1.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Arrow Damage. Base value = (1.0) = 100% default arrow damage
	 */
	public static final Attribute ARROW_DAMAGE = register("arrow_damage", new RangedAttribute("zenith:arrow_damage", 1.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Arrow Velocity. Base value = (1.0) = 100% default arrow velocity
	 */
	public static final Attribute ARROW_VELOCITY = register("arrow_velocity", new RangedAttribute("zenith:arrow_velocity", 1.0D, 0.0D, 1024.0D).setSyncable(true));
	/**
	 * Experience mulitplier, from killing mobs or breaking ores. Base value = (1.0) = 100% xp gained.
	 */
	public static final Attribute EXPERIENCE_GAINED = register("experience_gained", new RangedAttribute("zenith:experience_gained", 1.0D, 0.0D, 1024.0D).setSyncable(true));





	public static void init() {
		reload(false);



		AdventureEvents.init();
		//BossEvents.init();
	//	AffixManager.init();
	//	GemManager.init();
		//AffixLootManager.init();
		//BossArmorManager.init();
		//BossItemManager.init();
		//RandomSpawnerManager.init();
		//LootRarityManager.init();
		//MinibossManager.init();
		//AdventureGeneration.init();
	/*	Zenith.HELPER.registerProvider(f -> {
			RecipeHelper.addRecipe(new SocketingRecipe());
			RecipeHelper.addRecipe(new ExpulsionRecipe());
			RecipeHelper.addRecipe(new ExtractionRecipe());
		});*/
			LootSystem.defaultBlockTable(AdventureModule.REFORGING_TABLE);
			LootSystem.defaultBlockTable(AdventureModule.SALVAGING_TABLE);
			LootSystem.defaultBlockTable(AdventureModule.GEM_CUTTING_TABLE);

		//	Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(Zenith.MODID, "random_affix_item"), AffixLootPoolEntry.TYPE);
		//	Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(Zenith.MODID, "random_gem"), GemLootPoolEntry.TYPE);
			//Exclusion.initSerializers();
			GemBonus.initCodecs();


		//register(SocketingRecipe.Serializer.INSTANCE, SOCKETING_SERIALIZER);
		//register(ExpulsionRecipe.Serializer.INSTANCE, EXPULSION_SERIALIZER);
		//register(ExtractionRecipe.Serializer.INSTANCE, EXTRACTION_SERIALIZER);
	}


	public void register(Registry<Feature<?>> e) {
	//	e.register(BossDungeonFeature.INSTANCE, "boss_dng");
	//	e.register(BossDungeonFeature2.INSTANCE, "boss_dng_2");
	//	e.register(RogueSpawnerFeature.INSTANCE, "rogue_spawner");
		//NYI e.getRegistry().register(TroveFeature.INSTANCE, "trove");
		//NYI e.getRegistry().register(TomeTowerFeature.INSTANCE, "tome_tower");
	}


	public void items(Registry<Item> e) { /*
		for (LootRarity r : LootRarity.values()) {
			if (r == LootRarity.ANCIENT) continue;
			Item material = new SalvageItem(r, new Item.Properties().tab(Zenith.APOTH_GROUP));
			e.getRegistry().register(material, r.id() + "_material");
			RARITY_MATERIALS.put(r, material);
		}*/
	}

	//Blocks

	public static final Block BOSS_SPAWNER = register("boss_spawner", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
	public static final Block REFORGING_TABLE = register("reforging_table", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
	public static final Block SALVAGING_TABLE = register("salvaging_table", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
	public static final Block GEM_CUTTING_TABLE = register("gem_cutting_table", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));

	//Items
		public static final Item GEM = register("gem", new GemItem(new Item.Properties().stacksTo(1).tab(Zenith.ZENITH_GROUP)));
		//public static final Item BOSS_SUMMONER = register("boss_summoner", new BossSummonerItem(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item GEM_DUST = register("gem_dust", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item VIAL_OF_EXTRACTION = register("vial_of_extraction", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item VIAL_OF_EXPULSION = register("vial_of_expulsion", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item BOSS_SPAWNER_ITEM = register("boss_spawner", new BlockItem(BOSS_SPAWNER, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item REFORGING_TABLE_ITEM = register("reforging_table", new BlockItem(REFORGING_TABLE, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item SALVAGING_TABLE_ITEM = register("salvaging_table", new BlockItem(SALVAGING_TABLE, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item GEM_CUTTING_TABLE_ITEM = register("gem_cutting_table", new BlockItem(GEM_CUTTING_TABLE, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item SIGIL_OF_SOCKETING = register("sigil_of_socketing", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item SUPERIOR_SIGIL_OF_SOCKETING = register("superior_sigil_of_socketing", new GlowyItem(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item SIGIL_OF_ENHANCEMENT = register("sigil_of_enhancement", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
	public static final Item SUPERIOR_SIGIL_OF_ENHANCEMENT = register("superior_sigil_of_enhancement", new GlowyItem(new Item.Properties().tab(Zenith.ZENITH_GROUP)));

	//Tiles
//	public static final BlockEntityType<BossSpawnerTile> BOSS_SPAWNER_TILE = register("library", FabricBlockEntityTypeBuilder.create(BossSpawnerTile::new, BOSS_SPAWNER).build(null));
//	public static final BlockEntityType<ReforgingTableTile> REFORGING_TABLE_TILE = register("ender_library", FabricBlockEntityTypeBuilder.create(ReforgingTableTile::new, REFORGING_TABLE).build(null));

	//Recipes
/*	public static final RecipeType<SocketingRecipe> SOCKETING_RECIPE = ZenithUtil.makeRecipeType("zenith:socketing");
	public static final RecipeType<ExpulsionRecipe> SOCKETING_RECIPE = ZenithUtil.makeRecipeType("zenith:expulsion");
	public static final RecipeType<ExtractionRecipe> SOCKETING_RECIPE = ZenithUtil.makeRecipeType("zenith:extraction");
	public static final RecipeType<AddSocketsRecipe> SOCKETING_RECIPE = ZenithUtil.makeRecipeType("zenith:add_sockets");
	public static final RecipeType<SalvagingRecipe> SOCKETING_RECIPE = ZenithUtil.makeRecipeType("zenith:salvaging");
*/

	//Containers
//	public static final MenuType<EnchLibraryContainer> REFORGING_MENU = register("reforging", new ExtendedScreenHandlerType<>(ReforgingMenu::new));
//	public static final MenuType<ZenithEnchantContainer> SALVAGING_MENU = register("salvage", new MenuType<>(SalvagingMenu::new));
//	public static final MenuType<ZenithEnchantContainer> GEM_CUTTING_MENU = register("gem_cutting", new MenuType<>(GemCuttingMenu::new));

	//Affixes

	public static final DynamicRegistryObject<SocketAffix> SOCKET = AffixManager.INSTANCE.makeObj(Zenith.loc("socket"));
	//public static final DynamicRegistryObject<DurableAffix> DURABLE = AffixManager.INSTANCE.makeObj(Zenith.loc("durable"));
	// Real affixes
	public static final DynamicRegistryObject<MagicalArrowAffix> MAGICAL = AffixManager.INSTANCE.makeObj(Zenith.loc("ranged/special/magical"));
	//public static final DynamicRegistryObject<FestiveAffix> FESTIVE = AffixManager.INSTANCE.makeObj(Zenith.loc("sword/special/festive"));
	//public static final DynamicRegistryObject<TelepathicAffix> TELEPATHIC = AffixManager.INSTANCE.makeObj(Zenith.loc("telepathic"));
	//public static final DynamicRegistryObject<OmneticAffix> OMNETIC = AffixManager.INSTANCE.makeObj(Zenith.loc("breaker/special/omnetic"));
	//public static final DynamicRegistryObject<RadialAffix> RADIAL = AffixManager.INSTANCE.makeObj(Zenith.loc("breaker/special/radial"));

	/*
	public void miscRegistration(RegisterEvent e) {
		if (e.getForgeRegistry() == (Object) Registry.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get()) {
			e.getForgeRegistry().register("gems", GemLootModifier.CODEC);
			e.getForgeRegistry().register("affix_loot", AffixLootModifier.CODEC);
			e.getForgeRegistry().register("affix_conversion", AffixConvertLootModifier.CODEC);
		}
		if (e.getForgeRegistry() == (Object) ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get()) {
			e.getForgeRegistry().register("blacklist", AdventureGeneration.BlackistModifier.CODEC);
		}
	}*/


	/**
	 * Loads all configurable data for the deadly module.
	 */
	public static void reload(boolean e) {
		Configuration mainConfig = new Configuration(new File(Zenith.configDir, "adventure.cfg"));
		Configuration nameConfig = new Configuration(new File(Zenith.configDir, "names.cfg"));
		AdventureConfig.load(mainConfig);
		NameHelper.load(nameConfig);
		if (mainConfig.hasChanged()) mainConfig.save();
		if (nameConfig.hasChanged()) nameConfig.save();
	}

	public static final boolean DEBUG = false;

	public static void debugLog(BlockPos pos, String name) {
		if (DEBUG) AdventureModule.LOGGER.info("Generated a {} at {} {} {}", name, pos.getX(), pos.getY(), pos.getZ());
	}

	public static class ApothUpgradeRecipe extends UpgradeRecipe {

		public ApothUpgradeRecipe(ResourceLocation pId, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
			super(pId, pBase, pAddition, pResult);
		}
	}


	//private static Affix register(String registry, Affix affix){

	//}
	private static Attribute register(String registry, Attribute attribute) {
		return Registry.register(Registry.ATTRIBUTE, new ResourceLocation(Zenith.MODID, registry), attribute);
	}

	private static Item register(String name, Item item) {
		return Registry.register(Registry.ITEM, new ResourceLocation(Zenith.MODID, name), item);
	}

	private static Block register(String name, Block block) {
		return Registry.register(Registry.BLOCK, new ResourceLocation(Zenith.MODID, name), block);
	}

	private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(ResourceLocation id, S serializer) {
		return Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
	}
}