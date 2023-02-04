package safro.zenith.ench;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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
import safro.zenith.ench.objects.GlowyItem;
import safro.zenith.ench.objects.ScrappingTomeItem;
import safro.zenith.ench.objects.TomeItem;
import safro.zenith.ench.replacements.BaneEnchant;
import safro.zenith.ench.replacements.DefenseEnchant;
import safro.zenith.ench.table.ApothEnchantContainer;
import safro.zenith.ench.table.EnchantingRecipe;
import safro.zenith.ench.table.KeepNBTEnchantingRecipe;
import safro.zenith.util.ApotheosisUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchModule {
    public static final Map<Enchantment, EnchantmentInfo> ENCHANTMENT_INFO = new HashMap<>();
    public static final Object2IntMap<Enchantment> ENCH_HARD_CAPS = new Object2IntOpenHashMap<>();
    public static final String ENCH_HARD_CAP_IMC = "set_ench_hard_cap";
    public static final Logger LOGGER = LogManager.getLogger("Zenith : Enchantment");
    public static final List<TomeItem> TYPED_BOOKS = new ArrayList<>();
    public static final EquipmentSlot[] ARMOR = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    public static final RecipeType<EnchantingRecipe> INFUSION_RECIPE = ApotheosisUtil.makeRecipeType("zenith:enchanting");

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

    // Items
    public static final Item PRISMATIC_WEB = register("prismatic_web", new Item(new Item.Properties().tab(Zenith.APOTH_GROUP)));
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
    public static final Item HELLSHELF_ITEM = register("hellshelf", new BlockItem(HELLSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item INFUSED_HELLSHELF_ITEM = register("infused_hellshelf", new GlowyItem(INFUSED_HELLSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item BLAZING_HELLSHELF_ITEM = register("blazing_hellshelf", new BlockItem(BLAZING_HELLSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item GLOWING_HELLSHELF_ITEM = register("glowing_hellshelf", new BlockItem(GLOWING_HELLSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item SEASHELF_ITEM = register("seashelf", new BlockItem(SEASHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item INFUSED_SEASHELF_ITEM = register("infused_seashelf", new GlowyItem(INFUSED_SEASHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item CRYSTAL_SEASHELF_ITEM = register("crystal_seashelf", new BlockItem(CRYSTAL_SEASHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item HEART_SEASHELF_ITEM = register("heart_seashelf", new BlockItem(HEART_SEASHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item ENDSHELF_ITEM = register("endshelf", new BlockItem(ENDSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item PEARL_ENDSHELF_ITEM = register("pearl_endshelf", new BlockItem(PEARL_ENDSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item DRACONIC_ENDSHELF_ITEM = register("draconic_endshelf", new BlockItem(DRACONIC_ENDSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item BEESHELF_ITEM = register("beeshelf", new BlockItem(BEESHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item MELONSHELF_ITEM = register("melonshelf", new BlockItem(MELONSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item LIBRARY_ITEM = register("library", new BlockItem(LIBRARY, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item RECTIFIER_ITEM = register("rectifier", new BlockItem(RECTIFIER, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item RECTIFIER_T2_ITEM = register("rectifier_t2", new BlockItem(RECTIFIER_T2, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item RECTIFIER_T3_ITEM = register("rectifier_t3", new BlockItem(RECTIFIER_T3, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item SIGHTSHELF_ITEM = register("sightshelf", new BlockItem(SIGHTSHELF, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item SIGHTSHELF_T2_ITEM = register("sightshelf_t2", new BlockItem(SIGHTSHELF_T2, new Item.Properties().tab(Zenith.APOTH_GROUP)));
    public static final Item INERT_TRIDENT = register("inert_trident", new Item(new Item.Properties().stacksTo(1).tab(Zenith.APOTH_GROUP)));
    public static final Item ENDER_LIBRARY_ITEM = register("ender_library", new BlockItem(ENDER_LIBRARY, new Item.Properties().tab(Zenith.APOTH_GROUP)));

    // Tiles
    public static final BlockEntityType<EnchLibraryTile.BasicLibraryTile> LIBRARY_TILE = register("library", FabricBlockEntityTypeBuilder.create(EnchLibraryTile.BasicLibraryTile::new, LIBRARY).build(null));
    public static final BlockEntityType<EnchLibraryTile.EnderLibraryTile> ENDER_LIBRARY_TILE = register("ender_library", FabricBlockEntityTypeBuilder.create(EnchLibraryTile.EnderLibraryTile::new, ENDER_LIBRARY).build(null));

    // Container
    public static final MenuType<EnchLibraryContainer> LIBRARY_CONTAINER = register("library", new ExtendedScreenHandlerType<>(EnchLibraryContainer::new));
    public static final MenuType<ApothEnchantContainer> ENCHANTING_TABLE_MENU = register("enchanting_table", new MenuType<>(ApothEnchantContainer::new));

    // Recipe Serializer
    public static final RecipeSerializer<EnchantingRecipe> ENCHANTING = register("enchanting", EnchantingRecipe.SERIALIZER);
    public static final RecipeSerializer<KeepNBTEnchantingRecipe> KEEP_NBT_ENCHANTING = register("keep_nbt_enchanting", KeepNBTEnchantingRecipe.SERIALIZER);

    public static void init() {
        reload(false);

        EnchModuleEvents.init();

        Ingredient pot = Zenith.potionIngredient(Potions.REGENERATION);
        Zenith.HELPER.addShaped(HELLSHELF, 3, 3, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Items.BLAZE_ROD, "c:bookshelves", pot, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS);
        Zenith.HELPER.addShaped(PRISMATIC_WEB, 3, 3, null, Items.PRISMARINE_SHARD, null, Items.PRISMARINE_SHARD, Blocks.COBWEB, Items.PRISMARINE_SHARD, null, Items.PRISMARINE_SHARD, null);
        ItemStack book = new ItemStack(Items.BOOK);
        ItemStack stick = new ItemStack(Items.STICK);
        ItemStack blaze = new ItemStack(Items.BLAZE_ROD);
        Zenith.HELPER.addShaped(new ItemStack(HELMET_TOME, 5), 3, 2, book, book, book, book, blaze, book);
        Zenith.HELPER.addShaped(new ItemStack(CHESTPLATE_TOME, 8), 3, 3, book, blaze, book, book, book, book, book, book, book);
        Zenith.HELPER.addShaped(new ItemStack(LEGGINGS_TOME, 7), 3, 3, book, null, book, book, blaze, book, book, book, book);
        Zenith.HELPER.addShaped(new ItemStack(BOOTS_TOME, 4), 3, 2, book, null, book, book, blaze, book);
        Zenith.HELPER.addShaped(new ItemStack(WEAPON_TOME, 2), 1, 3, book, book, new ItemStack(Items.BLAZE_POWDER));
        Zenith.HELPER.addShaped(new ItemStack(PICKAXE_TOME, 3), 3, 3, book, book, book, null, blaze, null, null, stick, null);
        Zenith.HELPER.addShaped(new ItemStack(FISHING_TOME, 2), 3, 3, null, null, blaze, null, stick, book, stick, null, book);
        Zenith.HELPER.addShaped(new ItemStack(BOW_TOME, 3), 3, 3, null, stick, book, blaze, null, book, null, stick, book);
        Zenith.HELPER.addShapeless(new ItemStack(OTHER_TOME, 6), book, book, book, book, book, book, blaze);
        Zenith.HELPER.addShaped(new ItemStack(SCRAP_TOME, 8), 3, 3, book, book, book, book, Blocks.ANVIL, book, book, book, book);
        Ingredient maxHellshelf = Ingredient.of(INFUSED_HELLSHELF);
        Zenith.HELPER.addShaped(BLAZING_HELLSHELF, 3, 3, null, Items.FIRE_CHARGE, null, Items.FIRE_CHARGE, maxHellshelf, Items.FIRE_CHARGE, Items.BLAZE_POWDER, Items.BLAZE_POWDER, Items.BLAZE_POWDER);
        Zenith.HELPER.addShaped(GLOWING_HELLSHELF, 3, 3, null, Blocks.GLOWSTONE, null, null, maxHellshelf, null, Blocks.GLOWSTONE, null, Blocks.GLOWSTONE);
        Zenith.HELPER.addShaped(SEASHELF, 3, 3, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Zenith.potionIngredient(Potions.WATER), "c:bookshelves", Items.PUFFERFISH, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS);
        Ingredient maxSeashelf = Ingredient.of(INFUSED_SEASHELF);
        Zenith.HELPER.addShaped(CRYSTAL_SEASHELF, 3, 3, null, Items.PRISMARINE_CRYSTALS, null, null, maxSeashelf, null, Items.PRISMARINE_CRYSTALS, null, Items.PRISMARINE_CRYSTALS);
        Zenith.HELPER.addShaped(HEART_SEASHELF, 3, 3, null, Items.HEART_OF_THE_SEA, null, Items.PRISMARINE_SHARD, maxSeashelf, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD);
        Zenith.HELPER.addShaped(ENDSHELF, 3, 3, Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICKS, Items.DRAGON_BREATH, "c:bookshelves", Items.ENDER_PEARL, Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICKS);
        Zenith.HELPER.addShaped(PEARL_ENDSHELF, 3, 3, Items.END_ROD, null, Items.END_ROD, Items.ENDER_PEARL, ENDSHELF, Items.ENDER_PEARL, Items.END_ROD, null, Items.END_ROD);
        Zenith.HELPER.addShaped(DRACONIC_ENDSHELF, 3, 3, null, Items.DRAGON_HEAD, null, Items.ENDER_PEARL, ENDSHELF, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL);
        Zenith.HELPER.addShaped(BEESHELF, 3, 3, Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB, Items.HONEY_BLOCK, "c:bookshelves", Items.HONEY_BLOCK, Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB);
        Zenith.HELPER.addShaped(MELONSHELF, 3, 3, Items.MELON, Items.MELON, Items.MELON, Items.GLISTERING_MELON_SLICE, "c:bookshelves", Items.GLISTERING_MELON_SLICE, Items.MELON, Items.MELON, Items.MELON);
        Zenith.HELPER.addShaped(LIBRARY, 3, 3, Blocks.ENDER_CHEST, INFUSED_HELLSHELF, Blocks.ENDER_CHEST, INFUSED_HELLSHELF, Blocks.ENCHANTING_TABLE, INFUSED_HELLSHELF, Blocks.ENDER_CHEST, INFUSED_HELLSHELF, Blocks.ENDER_CHEST);
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

    private static Item replacement(String name, Item item, Item original) {
        return Registry.registerMapping(Registry.ITEM, Registry.ITEM.getId(original), Zenith.MODID + ":" + name, item);
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
            //LOGGER.error("A mod has attempted to access enchantment information before Apotheosis init, this should not happen.");
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
     * Tries to find a max level for this enchantment.  This is used to scale up default levels to the Apoth cap.
     * Single-Level enchantments are not scaled.
     * Barring that, enchantments are scaled using the {@link EnchantmentInfo#defaultMin(Enchantment)} until it is >= 150
     */
    public static int getDefaultMax(Enchantment ench) {
        int level = ench.getMaxLevel();
        if (level == 1) return 1;
        EnchantmentInfo.PowerFunc func = EnchantmentInfo.defaultMin(ench);
        int minPower = func.getPower(level);
        if (minPower >= 150) return level;
        int lastPower = minPower;
        while (minPower < 150) {
            minPower = func.getPower(++level);
            if (lastPower == minPower) return level;
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
