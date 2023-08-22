package dev.shadowsoffire.apotheosis.ench;

import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchantmentInfo.PowerFunc;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile.BasicLibraryTile;
import dev.shadowsoffire.apotheosis.ench.objects.TypedShelfBlock.SculkShelfBlock;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryContainer;
import dev.shadowsoffire.apotheosis.ench.objects.TomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.TypedShelfBlock;
import dev.shadowsoffire.apotheosis.ench.replacements.BaneEnchant;
import dev.shadowsoffire.apotheosis.ench.table.*;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

public class EnchModule {

    public static ItemStack left;
    public static ItemStack right;
    public static ItemStack out;
    public static int cost;
    public static final Map<Enchantment, EnchantmentInfo> ENCHANTMENT_INFO = new HashMap<>();
    public static final Object2IntMap<Enchantment> ENCH_HARD_CAPS = new Object2IntOpenHashMap<>();
    public static final String ENCH_HARD_CAP_IMC = "set_ench_hard_cap";
    public static final Logger LOGGER = LogManager.getLogger("Apotheosis : Enchantment");
    public static final List<TomeItem> TYPED_BOOKS = new ArrayList<>();
    //public static final EquipmentSlot[] ARMOR = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    //public static final EnchantmentCategory HOE = EnchantmentCategory.create("HOE", i -> i instanceof HoeItem);
    //public static final EnchantmentCategory SHIELD = EnchantmentCategory.create("SHIELD", i -> i instanceof ShieldItem);
    //public static final EnchantmentCategory ANVIL = EnchantmentCategory.create("ANVIL", i -> i instanceof BlockItem && ((BlockItem) i).getBlock() instanceof AnvilBlock);
    //public static final EnchantmentCategory SHEARS = EnchantmentCategory.create("SHEARS", i -> i instanceof ShearsItem);
    //public static final EnchantmentCategory PICKAXE = EnchantmentCategory.create("PICKAXE", i -> i.canPerformAction(new ItemStack(i), ToolActions.PICKAXE_DIG));
    //public static final EnchantmentCategory AXE = EnchantmentCategory.create("AXE", i -> i.canPerformAction(new ItemStack(i), ToolActions.AXE_DIG));
    //public static final EnchantmentCategory CORE_ARMOR = EnchantmentCategory.create("CORE_ARMOR", i -> EnchantmentCategory.ARMOR_CHEST.canEnchant(i) || EnchantmentCategory.ARMOR_LEGS.canEnchant(i));
    static Configuration enchInfoConfig;

    public EnchModule() {

    }

    public void init() {
        this.reload(false);

        Ench.bootstrap();
/*
        Apotheosis.HELPER.registerProvider(factory -> { //take your own advice and use a damn json shadows >:(
            Ingredient pot = Apotheosis.potionIngredient(Potions.REGENERATION);
            factory.addShaped(Ench.Blocks.HELLSHELF, 3, 3, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Items.BLAZE_ROD, "forge:bookshelves", pot, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS,
                    Blocks.NETHER_BRICKS);
            factory.addShaped(Ench.Items.PRISMATIC_WEB, 3, 3, null, Items.PRISMARINE_SHARD, null, Items.PRISMARINE_SHARD, Blocks.COBWEB, Items.PRISMARINE_SHARD, null, Items.PRISMARINE_SHARD, null);
            ItemStack book = new ItemStack(Items.BOOK);
            ItemStack stick = new ItemStack(Items.STICK);
            ItemStack blaze = new ItemStack(Items.BLAZE_ROD);
            factory.addShaped(new ItemStack(Ench.Items.HELMET_TOME, 5), 3, 2, book, book, book, book, blaze, book);
            factory.addShaped(new ItemStack(Ench.Items.CHESTPLATE_TOME, 8), 3, 3, book, blaze, book, book, book, book, book, book, book);
            factory.addShaped(new ItemStack(Ench.Items.LEGGINGS_TOME, 7), 3, 3, book, null, book, book, blaze, book, book, book, book);
            factory.addShaped(new ItemStack(Ench.Items.BOOTS_TOME, 4), 3, 2, book, null, book, book, blaze, book);
            factory.addShaped(new ItemStack(Ench.Items.WEAPON_TOME, 2), 1, 3, book, book, new ItemStack(Items.BLAZE_POWDER));
            factory.addShaped(new ItemStack(Ench.Items.PICKAXE_TOME, 3), 3, 3, book, book, book, null, blaze, null, null, stick, null);
            factory.addShaped(new ItemStack(Ench.Items.FISHING_TOME, 2), 3, 3, null, null, blaze, null, stick, book, stick, null, book);
            factory.addShaped(new ItemStack(Ench.Items.BOW_TOME, 3), 3, 3, null, stick, book, blaze, null, book, null, stick, book);
            factory.addShapeless(new ItemStack(Ench.Items.OTHER_TOME, 6), book, book, book, book, book, book, blaze);
            factory.addShaped(new ItemStack(Ench.Items.SCRAP_TOME, 8), 3, 3, book, book, book, book, Blocks.ANVIL, book, book, book, book);
            Ingredient maxHellshelf = Ingredient.of(Ench.Blocks.INFUSED_HELLSHELF);
            factory.addShaped(Ench.Blocks.BLAZING_HELLSHELF, 3, 3, null, Items.FIRE_CHARGE, null, Items.FIRE_CHARGE, maxHellshelf, Items.FIRE_CHARGE, Items.BLAZE_POWDER, Items.BLAZE_POWDER, Items.BLAZE_POWDER);
            factory.addShaped(Ench.Blocks.GLOWING_HELLSHELF, 3, 3, null, Blocks.GLOWSTONE, null, null, maxHellshelf, null, Blocks.GLOWSTONE, null, Blocks.GLOWSTONE);
            factory.addShaped(Ench.Blocks.SEASHELF, 3, 3, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Apotheosis.potionIngredient(Potions.WATER), "forge:bookshelves", Items.PUFFERFISH,
                    Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS);
            Ingredient maxSeashelf = Ingredient.of(Ench.Blocks.INFUSED_SEASHELF);
            factory.addShaped(Ench.Blocks.CRYSTAL_SEASHELF, 3, 3, null, Items.PRISMARINE_CRYSTALS, null, null, maxSeashelf, null, Items.PRISMARINE_CRYSTALS, null, Items.PRISMARINE_CRYSTALS);
            factory.addShaped(Ench.Blocks.HEART_SEASHELF, 3, 3, null, Items.HEART_OF_THE_SEA, null, Items.PRISMARINE_SHARD, maxSeashelf, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD,
                    Items.PRISMARINE_SHARD);
            factory.addShaped(Ench.Blocks.PEARL_ENDSHELF, 3, 3, Items.END_ROD, null, Items.END_ROD, Items.ENDER_PEARL, Ench.Blocks.ENDSHELF, Items.ENDER_PEARL, Items.END_ROD, null, Items.END_ROD);
            factory.addShaped(Ench.Blocks.DRACONIC_ENDSHELF, 3, 3, null, Items.DRAGON_HEAD, null, Items.ENDER_PEARL, Ench.Blocks.ENDSHELF, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL);
            factory.addShaped(Ench.Blocks.BEESHELF, 3, 3, Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB, Items.HONEY_BLOCK, "forge:bookshelves", Items.HONEY_BLOCK, Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB);
            factory.addShaped(Ench.Blocks.MELONSHELF, 3, 3, Items.MELON, Items.MELON, Items.MELON, Items.GLISTERING_MELON_SLICE, "forge:bookshelves", Items.GLISTERING_MELON_SLICE, Items.MELON, Items.MELON, Items.MELON);
        });*/

        EnchModuleEvents.registerEvents();

    /*    e.enqueueWork(() -> {
            LootSystem.defaultBlockTable(Ench.Blocks.HELLSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.INFUSED_HELLSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.BLAZING_HELLSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.GLOWING_HELLSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.SEASHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.INFUSED_SEASHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.CRYSTAL_SEASHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.HEART_SEASHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.DORMANT_DEEPSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.DEEPSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.ECHOING_DEEPSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.SOUL_TOUCHED_DEEPSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.ECHOING_SCULKSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.SOUL_TOUCHED_SCULKSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.ENDSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.PEARL_ENDSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.DRACONIC_ENDSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.BEESHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.MELONSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.STONESHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.LIBRARY);
            LootSystem.defaultBlockTable(Ench.Blocks.RECTIFIER);
            LootSystem.defaultBlockTable(Ench.Blocks.RECTIFIER_T2);
            LootSystem.defaultBlockTable(Ench.Blocks.RECTIFIER_T3);
            LootSystem.defaultBlockTable(Ench.Blocks.SIGHTSHELF);
            LootSystem.defaultBlockTable(Ench.Blocks.SIGHTSHELF_T2);
            LootSystem.defaultBlockTable(Ench.Blocks.ENDER_LIBRARY);
            DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenseItemBehavior());

            TabFillingRegistry.register(Ench.Tabs.ENCH, Ench.Items.HELLSHELF, Ench.Items.INFUSED_HELLSHELF, Ench.Items.BLAZING_HELLSHELF, Ench.Items.GLOWING_HELLSHELF, Ench.Items.SEASHELF, Ench.Items.INFUSED_SEASHELF,
                    Ench.Items.CRYSTAL_SEASHELF, Ench.Items.HEART_SEASHELF, Ench.Items.DORMANT_DEEPSHELF, Ench.Items.DEEPSHELF, Ench.Items.ECHOING_DEEPSHELF, Ench.Items.SOUL_TOUCHED_DEEPSHELF, Ench.Items.ECHOING_SCULKSHELF,
                    Ench.Items.SOUL_TOUCHED_SCULKSHELF, Ench.Items.ENDSHELF, Ench.Items.PEARL_ENDSHELF, Ench.Items.DRACONIC_ENDSHELF, Ench.Items.BEESHELF, Ench.Items.MELONSHELF, Ench.Items.STONESHELF, Ench.Items.RECTIFIER,
                    Ench.Items.RECTIFIER_T2, Ench.Items.RECTIFIER_T3, Ench.Items.SIGHTSHELF, Ench.Items.SIGHTSHELF_T2, Ench.Items.LIBRARY, Ench.Items.ENDER_LIBRARY);

            TabFillingRegistry.register(Ench.Tabs.ENCH, Ench.Items.HELMET_TOME, Ench.Items.CHESTPLATE_TOME, Ench.Items.LEGGINGS_TOME, Ench.Items.BOOTS_TOME, Ench.Items.WEAPON_TOME, Ench.Items.BOW_TOME, Ench.Items.PICKAXE_TOME,
                    Ench.Items.FISHING_TOME, Ench.Items.OTHER_TOME, Ench.Items.SCRAP_TOME, Ench.Items.IMPROVED_SCRAP_TOME, Ench.Items.EXTRACTION_TOME);

            TabFillingRegistry.register(Ench.Tabs.ENCH, Ench.Items.PRISMATIC_WEB, Ench.Items.INERT_TRIDENT, Ench.Items.WARDEN_TENDRIL, Ench.Items.INFUSED_BREATH);

            fill(Ench.Tabs.ENCH, Enchantments.BERSERKERS_FURY, Enchantments.CHAINSAW, Enchantments.CHROMATIC, Enchantments.CRESCENDO, Enchantments.EARTHS_BOON, Enchantments.ENDLESS_QUIVER, Enchantments.EXPLOITATION,
                    Enchantments.GROWTH_SERUM, Enchantments.ICY_THORNS, Enchantments.KNOWLEDGE, Enchantments.LIFE_MENDING, Enchantments.MINERS_FERVOR, Enchantments.NATURES_BLESSING, Enchantments.OBLITERATION, Enchantments.REBOUNDING,
                    Enchantments.REFLECTIVE, Enchantments.SCAVENGER, Enchantments.SHIELD_BASH, Enchantments.SPEARFISHING, Enchantments.SPLITTING, Enchantments.STABLE_FOOTING, Enchantments.TEMPTING);
        });*/

        EnchantingStatRegistry.INSTANCE.registerToBus();
    }

    public void miscRegistration() {
        //Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS, (Apotheosis.loc("warden_tendril")), new WardenLootModifier());
    }

    public void tiles() {
        Apoth.registerBEType("library", new BlockEntityType<>(BasicLibraryTile::new, ImmutableSet.of(Ench.Blocks.LIBRARY), null));
        Apoth.registerBEType("ender_library", new BlockEntityType<>(BasicLibraryTile::new, ImmutableSet.of(Ench.Blocks.ENDER_LIBRARY), null));
        //e.getRegistry().register(new BlockEntityType<>(AnvilTile::new, ImmutableSet.of(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL), null), "anvil");
        //BlockEntityType.ENCHANTING_TABLE.factory = ApothEnchantTile::new;
        BlockEntityType.ENCHANTING_TABLE.validBlocks = ImmutableSet.of(Blocks.ENCHANTING_TABLE);
    }

    public void containers() {
        Apoth.registerMenu("enchanting_table", MenuUtil.type(ApothEnchantmentMenu::new));
        Apoth.registerMenu("library", MenuUtil.posType(EnchLibraryContainer::new));
    }

    public void recipeSerializers() {
        Apoth.registerSerializer("enchanting", EnchantingRecipe.SERIALIZER);
        Apoth.registerSerializer("keep_nbt_enchanting", KeepNBTEnchantingRecipe.SERIALIZER);
    }
    /* //TODO particles
    public void particles(Register<ParticleType<?>> e) {
        e.getRegistry().registerAll(
                new SimpleParticleType(false), "enchant_fire",
                new SimpleParticleType(false), "enchant_water",
                new SimpleParticleType(false), "enchant_sculk",
                new SimpleParticleType(false), "enchant_end");
    }*/

    /**
     * This handles IMC events for the enchantment module. <br>
     * Currently only one type is supported. A mod may pass a single {@link EnchantmentInstance} indicating the hard capped max level for an enchantment. <br>
     * That pair must use the method {link ENCH_HARD_CAP_IMC}.
     */
    /*
    public void handleIMC(InterModProcessEvent e) {
        e.getIMCStream(ENCH_HARD_CAP_IMC::equals).forEach(msg -> {
            try {
                EnchantmentInstance data = (EnchantmentInstance) msg.messageSupplier();
                if (data != null && data.enchantment != null && data.level > 0) {
                    ENCH_HARD_CAPS.put(data.enchantment, data.level);
                }
                else LOGGER.error("Failed to process IMC message with method {} from {} (invalid values passed).", msg.method(), msg.senderModId());
            }
            catch (Exception ex) {
                LOGGER.error("Exception thrown during IMC message with method {} from {}.", msg.method(), msg.senderModId());
                ex.printStackTrace();
            }
        });
    }*/

    public void blocks() {
        //        new ApothAnvilBlock(), new ResourceLocation("minecraft", "anvil"),
        //        new ApothAnvilBlock(), new ResourceLocation("minecraft", "chipped_anvil"),
        //        new ApothAnvilBlock(), new ResourceLocation("minecraft", "damaged_anvil"));
        //PlaceboUtil.registerOverride(Blocks.ENCHANTING_TABLE, new ApothEnchantBlock(), Apotheosis.MODID);
    }

    private static Block shelf(BlockBehaviour.Properties props, float strength) {
        return shelf(props, strength, () -> ParticleTypes.ENCHANT);
    }

    private static Block shelf(BlockBehaviour.Properties props, float strength, Supplier<? extends ParticleOptions> particle) {
        props.strength(strength);
        return new TypedShelfBlock(props, particle);
    }

    private static Block sculkShelf(float strength, Supplier<? extends ParticleOptions> particle) {
        var props = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(strength).randomTicks().requiresCorrectToolForDrops();
        return new SculkShelfBlock(props, particle);
    }

    public void items() {
                //e.getRegistry().registerAll(
                //new ApothAnvilItem(Blocks.ANVIL), new ResourceLocation("minecraft", "anvil"),
                //new ApothAnvilItem(Blocks.CHIPPED_ANVIL), new ResourceLocation("minecraft", "chipped_anvil"),
                //new ApothAnvilItem(Blocks.DAMAGED_ANVIL), new ResourceLocation("minecraft", "damaged_anvil"));
    }

    public void enchants() {
                registerEnch("bane_of_illagers", new BaneEnchant(Rarity.UNCOMMON, MobType.ILLAGER, EquipmentSlot.MAINHAND));
                //new BaneEnchant(Rarity.UNCOMMON, MobType.ARTHROPOD, EquipmentSlot.MAINHAND), new ResourceLocation("minecraft", "bane_of_arthropods"),
                //new BaneEnchant(Rarity.UNCOMMON, MobType.UNDEAD, EquipmentSlot.MAINHAND), new ResourceLocation("minecraft", "smite"),
                //new BaneEnchant(Rarity.COMMON, MobType.UNDEFINED, EquipmentSlot.MAINHAND), new ResourceLocation("minecraft", "sharpness"),

                //new DefenseEnchant(Rarity.COMMON, ProtectionEnchantment.Type.ALL, ARMOR), new ResourceLocation("minecraft", "protection"),
                //new DefenseEnchant(Rarity.UNCOMMON, ProtectionEnchantment.Type.FIRE, ARMOR), new ResourceLocation("minecraft", "fire_protection"),
                //new DefenseEnchant(Rarity.RARE, ProtectionEnchantment.Type.EXPLOSION, ARMOR), new ResourceLocation("minecraft", "blast_protection"),
                //new DefenseEnchant(Rarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ARMOR), new ResourceLocation("minecraft", "projectile_protection"),
                //new DefenseEnchant(Rarity.UNCOMMON, ProtectionEnchantment.Type.FALL, EquipmentSlot.FEET), new ResourceLocation("minecraft", "feather_falling"));
    }

    @SuppressWarnings("deprecation")
    public static EnchantmentInfo getEnchInfo(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ENCHANTMENT_INFO.computeIfAbsent(ench, EnchantmentInfo::new);

        EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);

        if (enchInfoConfig == null) { // Legitimate occurances can now happen, such as when vanilla calls fillItemGroup
            // LOGGER.error("A mod has attempted to access enchantment information before Apotheosis init, this should not happen.");
            // Thread.dumpStack();
            return new EnchantmentInfo(ench);
        }

        if (info == null) { // Should be impossible now.
            info = EnchantmentInfo.load(ench, enchInfoConfig);
            ENCHANTMENT_INFO.put(ench, info);
            if (enchInfoConfig.hasChanged()) enchInfoConfig.save();
            LOGGER.error("Had to late load enchantment info for {}, this is a bug in the mod {} as they are registering late!", BuiltInRegistries.ENCHANTMENT.getKey(ench), BuiltInRegistries.ENCHANTMENT.getKey(ench).getNamespace());
        }

        return info;
    }

    /**
     * Tries to find a max level for this enchantment. This is used to scale up default levels to the Apoth cap.
     * Single-Level enchantments are not scaled.
     * Barring that, enchantments are scaled using the {@link EnchantmentInfo#defaultMin(Enchantment)} until outside the default level space.
     */
    public static int getDefaultMax(Enchantment ench) {
        int level = ench.getMaxLevel();
        if (level == 1) return 1;
        PowerFunc minFunc = EnchantmentInfo.defaultMin(ench);
        int max = (int) (EnchantingStatRegistry.getAbsoluteMaxEterna() * 4);
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

    @SafeVarargs
    public static void fill(ResourceKey<CreativeModeTab> tab, Supplier<? extends Enchantment>... enchants) {
        Arrays.stream(enchants).map(EnchModule::enchFiller).forEach(filler -> TabFillingRegistry.register(filler, tab));
    }

    public static ITabFiller enchFiller(Supplier<? extends Enchantment> e) {
        return (tab, output) -> {
            Enchantment ench = e.get();
            int maxLevel = EnchHooks.getMaxLevel(ench);
            output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, maxLevel)), TabVisibility.PARENT_TAB_ONLY);
            for (int level = 1; level <= maxLevel; level++) {
                output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, level)), TabVisibility.SEARCH_TAB_ONLY);
            }
        };
    }

    public void reload(boolean e) {
        enchInfoConfig = new Configuration(new File(Apotheosis.configDir, "enchantments.cfg"));
        enchInfoConfig.setTitle("Apotheosis Enchantment Information");
        enchInfoConfig.setComment("This file contains configurable data for each enchantment.\nThe names of each category correspond to the registry names of every loaded enchantment.");
        ENCHANTMENT_INFO.clear();

        for (Enchantment ench : BuiltInRegistries.ENCHANTMENT) {
            ENCHANTMENT_INFO.put(ench, EnchantmentInfo.load(ench, enchInfoConfig));
        }

        for (Enchantment ench : BuiltInRegistries.ENCHANTMENT) {
            EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);
            for (int i = 1; i <= info.getMaxLevel(); i++)
                if (info.getMinPower(i) > info.getMaxPower(i))
                    LOGGER.warn("Enchantment {} has min/max power {}/{} at level {}, making this level unobtainable.", BuiltInRegistries.ENCHANTMENT.getKey(ench), info.getMinPower(i), info.getMaxPower(i), i);
        }

        if (!e && enchInfoConfig.hasChanged()) enchInfoConfig.save();
    }

    private static Enchantment registerEnch(String name, Enchantment ench) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, Apotheosis.loc(name), ench);
    }
}
