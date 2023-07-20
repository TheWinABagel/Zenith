package safro.zenith.ench;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import safro.zenith.Zenith;
import safro.zenith.api.config.Configuration;
import safro.zenith.ench.anvil.ObliterationEnchant;
import safro.zenith.ench.anvil.SplittingEnchant;
import safro.zenith.ench.enchantments.*;
import safro.zenith.ench.enchantments.corrupted.BerserkersFuryEnchant;
import safro.zenith.ench.enchantments.corrupted.LifeMendingEnchant;
import safro.zenith.ench.enchantments.masterwork.*;
import safro.zenith.ench.enchantments.twisted.ExploitationEnchant;
import safro.zenith.ench.enchantments.twisted.MinersFervorEnchant;
import safro.zenith.ench.library.EnchLibraryBlock;
import safro.zenith.ench.library.EnchLibraryContainer;
import safro.zenith.ench.library.EnchLibraryTile;
import safro.zenith.ench.objects.*;
import safro.zenith.ench.replacements.BaneEnchant;
import safro.zenith.ench.replacements.DefenseEnchant;
import safro.zenith.ench.table.EnchantingStatManager;
import safro.zenith.ench.table.ZenithEnchantContainer;
import safro.zenith.ench.table.EnchantingRecipe;
import safro.zenith.ench.table.KeepNBTEnchantingRecipe;
import safro.zenith.util.ZenithUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchModule {
    public static final Map<Enchantment, EnchantmentInfo> ENCHANTMENT_INFO = new HashMap<>();
    public static final Object2IntMap<Enchantment> ENCH_HARD_CAPS = new Object2IntOpenHashMap<>();
    public static final Logger LOGGER = LogManager.getLogger("Zenith : Enchantment");
    public static final List<TomeItem> TYPED_BOOKS = new ArrayList<>();
    public static final EquipmentSlot[] ARMOR = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    public static final RecipeType<EnchantingRecipe> INFUSION_RECIPE = ZenithUtil.makeRecipeType("zenith:enchanting");


    static Configuration enchInfoConfig;

    // Enchantments
    public static final Enchantment LIFE_MENDING = register("life_mending", new LifeMendingEnchant());
    public static final Enchantment CRESCENDO = register("crescendo", new CrescendoEnchant());
    public static final Enchantment CHAINSAW = register("chainsaw", new ChainsawEnchant());
    public static final Enchantment BES_FURY = register("berserkers_fury", new BerserkersFuryEnchant());
    public static final Enchantment EARTH_BOON = register("earths_boon", new EarthsBoonEnchant());
    public static final Enchantment ENDLESS_QUIVER = register("endless_quiver", new EndlessQuiverEnchant());
    public static final Enchantment KNOWLEDGE = register("knowledge", new KnowledgeEnchant());
    public static final Enchantment SCAVENGER = register("scavenger", new ScavengerEnchant());
    public static final Enchantment GROWTH_SERUM = register("growth_serum", new GrowthSerumEnchant());
    public static final Enchantment MINERS_FERVOR = register("miners_fervor", new MinersFervorEnchant());
    public static final Enchantment EXPLOITATION = register("exploitation", new ExploitationEnchant());
    public static final Enchantment CHROMATIC = register("chromatic", new ChromaticEnchant());
    public static final Enchantment ICY_THORNS = register("icy_thorns", new IcyThornsEnchant());
    public static final Enchantment INFUSION = register("infusion", new InertEnchantment());
    public static final Enchantment NATURES_BLESSING = register("natures_blessing", new NaturesBlessingEnchant());
    public static final Enchantment REBOUNDING = register("rebounding", new ReboundingEnchant());
    public static final Enchantment REFLECTIVE = register("reflective", new ReflectiveEnchant());
    public static final Enchantment SHIELD_BASH = register("shield_bash", new ShieldBashEnchant());
    public static final Enchantment SPEARFISHING = register("spearfishing", new SpearfishingEnchant());
    public static final Enchantment STABLE_FOOTING = register("stable_footing", new StableFootingEnchant());
    public static final Enchantment TEMPTING = register("tempting", new TemptingEnchant());
    public static final Enchantment BOI = register("bane_of_illagers", new BaneEnchant(Enchantment.Rarity.UNCOMMON, MobType.ILLAGER, EquipmentSlot.MAINHAND));
    public static final Enchantment OBLITERATION = register("obliteration", new ObliterationEnchant());
    public static final Enchantment SPLITTING = register("splitting", new SplittingEnchant());

    public static final Enchantment BOA = replacement("bane_of_arthropods", new BaneEnchant(Enchantment.Rarity.UNCOMMON, MobType.ARTHROPOD, EquipmentSlot.MAINHAND), Enchantments.BANE_OF_ARTHROPODS);
    public static final Enchantment SMITE = replacement("smite", new BaneEnchant(Enchantment.Rarity.UNCOMMON, MobType.UNDEAD, EquipmentSlot.MAINHAND), Enchantments.SMITE);
    public static final Enchantment SHARPNESS = replacement("sharpness", new BaneEnchant(Enchantment.Rarity.COMMON, MobType.UNDEFINED, EquipmentSlot.MAINHAND), Enchantments.SHARPNESS);
    public static final Enchantment PROTECTION = replacement("protection", new DefenseEnchant(Enchantment.Rarity.COMMON, ProtectionEnchantment.Type.ALL, ARMOR), Enchantments.ALL_DAMAGE_PROTECTION);
    public static final Enchantment FIRE_PROTECTION = replacement("fire_protection", new DefenseEnchant(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FIRE, ARMOR), Enchantments.FIRE_PROTECTION);
    public static final Enchantment BLAST_PROTECTION = replacement("blast_protection", new DefenseEnchant(Enchantment.Rarity.RARE, ProtectionEnchantment.Type.EXPLOSION, ARMOR), Enchantments.BLAST_PROTECTION);
    public static final Enchantment PROJECTILE_PROTECTION = replacement("projectile_protection", new DefenseEnchant(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ARMOR), Enchantments.PROJECTILE_PROTECTION);
    public static final Enchantment FEATHER_FALLING = replacement("feather_falling", new DefenseEnchant(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FALL, ARMOR), Enchantments.FALL_PROTECTION);

    // Blocks
    public static final Block LIBRARY = register("library", new EnchLibraryBlock(EnchLibraryTile.BasicLibraryTile::new, 16));
    public static final Block HELLSHELF = register("hellshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block INFUSED_HELLSHELF = register("infused_hellshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block BLAZING_HELLSHELF = register("blazing_hellshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block GLOWING_HELLSHELF = register("glowing_hellshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block SEASHELF = register("seashelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block INFUSED_SEASHELF = register("infused_seashelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block CRYSTAL_SEASHELF = register("crystal_seashelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block HEART_SEASHELF = register("heart_seashelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block ENDSHELF = register("endshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block PEARL_ENDSHELF = register("pearl_endshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block DRACONIC_ENDSHELF = register("draconic_endshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block BEESHELF = register("beeshelf", new Block(BlockBehaviour.Properties.of(Material.WOOD).strength(1.5F).sound(SoundType.WOOD)));
    public static final Block MELONSHELF = register("melonshelf", new Block(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(1.5F).sound(SoundType.WOOD)));
    public static final Block RECTIFIER = register("rectifier", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block RECTIFIER_T2 = register("rectifier_t2", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block RECTIFIER_T3 = register("rectifier_t3", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block SIGHTSHELF = register("sightshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block SIGHTSHELF_T2 = register("sightshelf_t2", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block ENDER_LIBRARY = register("ender_library", new EnchLibraryBlock(EnchLibraryTile.EnderLibraryTile::new, 31));
    public static final Block DORMANT_DEEPSHELF = register("dormant_deepshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block DEEPSHELF = register("deepshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block ECHOING_DEEPSHELF = register("echoing_deepshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block SOUL_TOUCHED_DEEPSHELF = register("soul_touched_deepshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block ECHOING_SCULKSHELF = register("echoing_sculkshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block SOUL_TOUCHED_SCULKSHELF = register("soul_touched_sculkshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));
    public static final Block STONESHELF = register("stoneshelf", new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F).sound(SoundType.STONE)));

    // Items
    public static final Item PRISMATIC_WEB = register("prismatic_web", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item WARDEN_TENDRIL = register("warden_tendril", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item INFUSED_BREATH = register("infused_breath", new Item(new Item.Properties().tab(Zenith.ZENITH_GROUP).rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item OTHER_TOME = register("other_tome", new TomeItem(Items.AIR, null));
    public static final Item HELMET_TOME = register("helmet_tome", new TomeItem(Items.DIAMOND_HELMET, EnchantmentCategory.ARMOR_HEAD));
    public static final Item CHESTPLATE_TOME = register("chestplate_tome", new TomeItem(Items.DIAMOND_CHESTPLATE, EnchantmentCategory.ARMOR_CHEST));
    public static final Item LEGGINGS_TOME = register("leggings_tome", new TomeItem(Items.DIAMOND_LEGGINGS, EnchantmentCategory.ARMOR_LEGS));
    public static final Item BOOTS_TOME = register("boots_tome", new TomeItem(Items.DIAMOND_BOOTS, EnchantmentCategory.ARMOR_FEET));
    public static final Item WEAPON_TOME = register("weapon_tome", new TomeItem(Items.DIAMOND_SWORD, EnchantmentCategory.WEAPON));
    public static final Item PICKAXE_TOME = register("pickaxe_tome", new TomeItem(Items.DIAMOND_PICKAXE, EnchantmentCategory.DIGGER));
    public static final Item FISHING_TOME = register("fishing_tome", new TomeItem(Items.FISHING_ROD, EnchantmentCategory.FISHING_ROD));
    public static final Item BOW_TOME = register("bow_tome", new TomeItem(Items.BOW, EnchantmentCategory.BOW));
    public static final Item SCRAP_TOME = register("scrap_tome", new ScrappingTomeItem());
    public static final Item IMPROVED_SCRAP_TOME = register("improved_scrap_tome", new ImprovedScrappingTomeItem());
    public static final Item EXTRACTION_TOME = register("extraction_tome", new ExtractionTomeItem());

    public static final Item HELLSHELF_ITEM = register("hellshelf", new BlockItem(HELLSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item INFUSED_HELLSHELF_ITEM = register("infused_hellshelf", new GlowyBlock(INFUSED_HELLSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item BLAZING_HELLSHELF_ITEM = register("blazing_hellshelf", new BlockItem(BLAZING_HELLSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item GLOWING_HELLSHELF_ITEM = register("glowing_hellshelf", new BlockItem(GLOWING_HELLSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item SEASHELF_ITEM = register("seashelf", new BlockItem(SEASHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item INFUSED_SEASHELF_ITEM = register("infused_seashelf", new GlowyBlock(INFUSED_SEASHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item CRYSTAL_SEASHELF_ITEM = register("crystal_seashelf", new BlockItem(CRYSTAL_SEASHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item HEART_SEASHELF_ITEM = register("heart_seashelf", new BlockItem(HEART_SEASHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item ENDSHELF_ITEM = register("endshelf", new BlockItem(ENDSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item PEARL_ENDSHELF_ITEM = register("pearl_endshelf", new BlockItem(PEARL_ENDSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item DRACONIC_ENDSHELF_ITEM = register("draconic_endshelf", new BlockItem(DRACONIC_ENDSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item BEESHELF_ITEM = register("beeshelf", new BlockItem(BEESHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item MELONSHELF_ITEM = register("melonshelf", new BlockItem(MELONSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item LIBRARY_ITEM = register("library", new BlockItem(LIBRARY, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item RECTIFIER_ITEM = register("rectifier", new BlockItem(RECTIFIER, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item RECTIFIER_T2_ITEM = register("rectifier_t2", new BlockItem(RECTIFIER_T2, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item RECTIFIER_T3_ITEM = register("rectifier_t3", new BlockItem(RECTIFIER_T3, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item SIGHTSHELF_ITEM = register("sightshelf", new BlockItem(SIGHTSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item SIGHTSHELF_T2_ITEM = register("sightshelf_t2", new BlockItem(SIGHTSHELF_T2, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item INERT_TRIDENT = register("inert_trident", new Item(new Item.Properties().stacksTo(1).tab(Zenith.ZENITH_GROUP)));
    public static final Item ENDER_LIBRARY_ITEM = register("ender_library", new BlockItem(ENDER_LIBRARY, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item DORMANT_DEEPSHELF_ITEM = register("dormant_deepshelf", new BlockItem(DORMANT_DEEPSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item DEEPSHELF_ITEM = register("deepshelf", new GlowyBlock(DEEPSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item ECHOING_DEEPSHELF_ITEM = register("echoing_deepshelf", new BlockItem(ECHOING_DEEPSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item SOUL_TOUCHED_DEEPSHELF_ITEM = register("soul_touched_deepshelf", new BlockItem(SOUL_TOUCHED_DEEPSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item ECHOING_SCULKSHELF_ITEM = register("echoing_sculkshelf", new BlockItem(ECHOING_SCULKSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item SOUL_TOUCHED_SCULKSHELF_ITEM = register("soul_touched_sculkshelf", new BlockItem(SOUL_TOUCHED_SCULKSHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));
    public static final Item STONESHELF_ITEM = register("stoneshelf", new BlockItem(STONESHELF, new Item.Properties().tab(Zenith.ZENITH_GROUP)));

    // Block Tags
    public static final TagKey<Block> UNBREAKABLE_ANVIL = registerBlockTag("unbreakable_anvil");

    // Tiles
    public static final BlockEntityType<EnchLibraryTile.BasicLibraryTile> LIBRARY_TILE = register("library", FabricBlockEntityTypeBuilder.create(EnchLibraryTile.BasicLibraryTile::new, LIBRARY).build(null));
    public static final BlockEntityType<EnchLibraryTile.EnderLibraryTile> ENDER_LIBRARY_TILE = register("ender_library", FabricBlockEntityTypeBuilder.create(EnchLibraryTile.EnderLibraryTile::new, ENDER_LIBRARY).build(null));

    // Container
    public static final MenuType<EnchLibraryContainer> LIBRARY_CONTAINER = register("library", new ExtendedScreenHandlerType<>(EnchLibraryContainer::new));
    public static final MenuType<ZenithEnchantContainer> ENCHANTING_TABLE_MENU = register("enchanting_table", new MenuType<>(ZenithEnchantContainer::new));

    // Recipe Serializer
    public static final RecipeSerializer<EnchantingRecipe> ENCHANTING = register("enchanting", EnchantingRecipe.SERIALIZER);
    public static final RecipeSerializer<KeepNBTEnchantingRecipe> KEEP_NBT_ENCHANTING = register("keep_nbt_enchanting", KeepNBTEnchantingRecipe.SERIALIZER);

    public static void init() {
        reload(false);

        EnchModuleEvents.init();
    }


    private static TagKey<Block> registerBlockTag(String string) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(string));
    }
    private static TagKey<Item> registerItemTag(String string) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(string));
    }
    private static Enchantment register(String name, Enchantment ench) {
        return Registry.register(Registry.ENCHANTMENT, new ResourceLocation(Zenith.MODID, name), ench);
    }

    private static Enchantment replacement(String name, Enchantment ench, Enchantment original) {
        return Registry.registerMapping(Registry.ENCHANTMENT, Registry.ENCHANTMENT.getId(original), Zenith.MODID + ":" + name, ench);
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Zenith.MODID, name), item);
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, new ResourceLocation(Zenith.MODID, name), block);
    }


    private static<T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> be) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Zenith.MODID, name), be);
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(Zenith.MODID, id), serializer);
    }

    private static <M extends MenuType<T>, T extends AbstractContainerMenu> M register(String id, M menu) {
        return Registry.register(Registry.MENU, new ResourceLocation(Zenith.MODID, id), menu);
    }

    public static EnchantmentInfo getEnchInfo(Enchantment ench) {
        if (!Zenith.enableEnch) return ENCHANTMENT_INFO.computeIfAbsent(ench, EnchantmentInfo::new);

        EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);

        if (enchInfoConfig == null) { //Legitimate occurances can now happen, such as when vanilla calls fillItemGroup
            //LOGGER.error("A mod has attempted to access enchantment information before Zenith init, this should not happen.");
            //Thread.dumpStack();
            return new EnchantmentInfo(ench);
        }

        if (info == null) {
            info = EnchantmentInfo.load(ench, enchInfoConfig);
            ENCHANTMENT_INFO.put(ench, info);
            if (enchInfoConfig.hasChanged()) enchInfoConfig.save();
            LOGGER.error("Had to late load enchantment info for {}, this is a bug in the mod {} as they are registering late!", Registry.ENCHANTMENT.getKey(ench), Registry.ENCHANTMENT.getKey(ench).getNamespace());
        }

        return info;
    }

    /**
     * Tries to find a max level for this enchantment.  This is used to scale up default levels to the Zenith cap.
     * Single-Level enchantments are not scaled.
     * Barring that, enchantments are scaled using the {@link EnchantmentInfo#defaultMin(Enchantment)} until it is >= 150
     */
    public static int getDefaultMax(Enchantment ench) {
        int level = ench.getMaxLevel();
        if (level == 1) return 1;
        EnchantmentInfo.PowerFunc minFunc = EnchantmentInfo.defaultMin(ench);
        int max = (int) (EnchantingStatManager.getAbsoluteMaxEterna() * 4);
        int minPower = minFunc.getPower(level);
        if (minPower >= max) return level;
        int lastPower = minPower;
        while (minPower < max) {
            minPower = minFunc.getPower(++level);
            if (lastPower == minPower) return level;
            if (minPower > max) return level - 1;
            lastPower = minPower;
        }
        return level;
    }

    public static void reload(boolean e) {
        enchInfoConfig = new Configuration(new File(Zenith.configDir, "enchantments.cfg"));
        enchInfoConfig.setTitle("Zenith Enchantment Information");
        enchInfoConfig.setComment("This file contains configurable data for each enchantment.\nThe names of each category correspond to the registry names of every loaded enchantment.");
        ENCHANTMENT_INFO.clear();

        for (Enchantment ench : Registry.ENCHANTMENT) {
            ENCHANTMENT_INFO.put(ench, EnchantmentInfo.load(ench, enchInfoConfig));
        }

        for (Enchantment ench : Registry.ENCHANTMENT) {
            EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);
            for (int i = 1; i <= info.getMaxLevel(); i++)
                if (info.getMinPower(i) > info.getMaxPower(i)) LOGGER.error("Enchantment {} has min/max power {}/{} at level {}, making this level unobtainable.", Registry.ENCHANTMENT.getKey(ench), info.getMinPower(i), info.getMaxPower(i), i);
        }

        if (!e && enchInfoConfig.hasChanged()) enchInfoConfig.save();
    }
}
