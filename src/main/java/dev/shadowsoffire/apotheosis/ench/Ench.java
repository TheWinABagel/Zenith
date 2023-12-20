package dev.shadowsoffire.apotheosis.ench;

import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.anvil.ObliterationEnchant;
import dev.shadowsoffire.apotheosis.ench.anvil.SplittingEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.*;
import dev.shadowsoffire.apotheosis.ench.enchantments.corrupted.BerserkersFuryEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.corrupted.LifeMendingEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.*;
import dev.shadowsoffire.apotheosis.ench.enchantments.twisted.ExploitationEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.twisted.MinersFervorEnchant;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryBlock;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryContainer;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile.BasicLibraryTile;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile.EnderLibraryTile;
import dev.shadowsoffire.apotheosis.ench.objects.*;
import dev.shadowsoffire.apotheosis.ench.objects.TypedShelfBlock.SculkShelfBlock;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class Ench {

    public static final class Blocks {

        public static final Block BEESHELF = woodShelf("beeshelf", MapColor.COLOR_YELLOW, 0.75F, ParticleTypes.ENCHANT);

        public static final Block BLAZING_HELLSHELF = stoneShelf("blazing_hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block CRYSTAL_SEASHELF = stoneShelf("crystal_seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Block DEEPSHELF = stoneShelf("deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Block DORMANT_DEEPSHELF = stoneShelf("dormant_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Block DRACONIC_ENDSHELF = stoneShelf("draconic_endshelf", MapColor.SAND, 5F, Particles.ENCHANT_END);

        public static final Block ECHOING_DEEPSHELF = stoneShelf("echoing_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Block ECHOING_SCULKSHELF = sculkShelf("echoing_sculkshelf");

        public static final EnchLibraryBlock ENDER_LIBRARY = new EnchLibraryBlock(EnderLibraryTile::new, 31);

        public static final Block ENDSHELF = stoneShelf("endshelf", MapColor.SAND, 4.5F, Particles.ENCHANT_END);

        public static final Block GLOWING_HELLSHELF = stoneShelf("glowing_hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block HEART_SEASHELF = stoneShelf("heart_seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Block HELLSHELF = stoneShelf("hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block INFUSED_HELLSHELF = stoneShelf("infused_hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block INFUSED_SEASHELF = stoneShelf("infused_seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final EnchLibraryBlock LIBRARY = new EnchLibraryBlock(BasicLibraryTile::new, 16);

        public static final Block MELONSHELF = woodShelf("melonshelf", MapColor.COLOR_GREEN, 0.75F, ParticleTypes.ENCHANT);

        public static final Block PEARL_ENDSHELF = stoneShelf("pearl_endshelf", MapColor.SAND, 4.5F, Particles.ENCHANT_END);

        public static final Block RECTIFIER = stoneShelf("rectifier", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Block RECTIFIER_T2 = stoneShelf("rectifier_t2", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block RECTIFIER_T3 = stoneShelf("rectifier_t3", MapColor.SAND, 1.5F, Particles.ENCHANT_END);

        public static final Block SEASHELF = stoneShelf("seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Block SIGHTSHELF = stoneShelf("sightshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block SIGHTSHELF_T2 = stoneShelf("sightshelf_t2", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block SOUL_TOUCHED_DEEPSHELF = stoneShelf("soul_touched_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Block SOUL_TOUCHED_SCULKSHELF = sculkShelf("soul_touched_sculkshelf");

        public static final Block STONESHELF = stoneShelf("stoneshelf", MapColor.STONE, 1.75F, ParticleTypes.ENCHANT);

        public static final Block FILTERING_SHELF = new FilteringShelfBlock(Block.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.STONE).strength(1.75F).requiresCorrectToolForDrops());

        public static final Block TREASURE_SHELF = new TreasureShelfBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(1.75F).requiresCorrectToolForDrops());

        private static void init() {
            reg(BEESHELF, "beeshelf");
            reg(BLAZING_HELLSHELF, "blazing_hellshelf");
            reg(CRYSTAL_SEASHELF, "crystal_seashelf");
            reg(DEEPSHELF, "deepshelf");
            reg(DORMANT_DEEPSHELF, "dormant_deepshelf");
            reg(DRACONIC_ENDSHELF, "draconic_endshelf");
            reg(ECHOING_DEEPSHELF, "echoing_deepshelf");
            reg(ECHOING_SCULKSHELF, "echoing_sculkshelf");
            reg(ENDER_LIBRARY, "ender_library");
            reg(ENDSHELF, "endshelf");
            reg(GLOWING_HELLSHELF, "glowing_hellshelf");
            reg(HEART_SEASHELF, "heart_seashelf");
            reg(HELLSHELF, "hellshelf");
            reg(INFUSED_HELLSHELF, "infused_hellshelf");
            reg(INFUSED_SEASHELF, "infused_seashelf");
            reg(LIBRARY, "library");
            reg(MELONSHELF, "melonshelf");
            reg(PEARL_ENDSHELF, "pearl_endshelf");
            reg(RECTIFIER, "rectifier");
            reg(RECTIFIER_T2, "rectifier_t2");
            reg(RECTIFIER_T3, "rectifier_t3");
            reg(SEASHELF, "seashelf");
            reg(SIGHTSHELF, "sightshelf");
            reg(SIGHTSHELF_T2, "sightshelf_t2");
            reg(SOUL_TOUCHED_DEEPSHELF, "soul_touched_deepshelf");
            reg(SOUL_TOUCHED_SCULKSHELF, "soul_touched_sculkshelf");
            reg(STONESHELF, "stoneshelf");
            reg(FILTERING_SHELF, "filtering_shelf");
            reg(TREASURE_SHELF, "treasure_shelf");
        //    DispenserBlock.registerBehavior(net.minecraft.world.item.Items.SHEARS, new ShearsDispenseItemBehavior());
        }

        private static Block sculkShelf(String id) {
            return new SculkShelfBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).randomTicks().requiresCorrectToolForDrops().strength(3.5F), Particles.ENCHANT_SCULK);
        }

        private static Block stoneShelf(String id, MapColor color, float strength, SimpleParticleType particle) {
            return new TypedShelfBlock(Block.Properties.of().requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(color).strength(strength), particle);
        }

        private static Block woodShelf(String id, MapColor color, float strength, SimpleParticleType particle) {
            return new TypedShelfBlock(Block.Properties.of().sound(SoundType.WOOD).mapColor(color).strength(strength), particle);
        }

        private static void reg(Block item, String id){
            Registry.register(BuiltInRegistries.BLOCK, Apotheosis.loc(id), item);
        }

    }

    public static class Items {

        public static final BlockItem BEESHELF = new BlockItem(Blocks.BEESHELF, new Item.Properties());

        public static final BlockItem BLAZING_HELLSHELF = new BlockItem(Blocks.BLAZING_HELLSHELF, new Item.Properties());

        public static final TomeItem BOOTS_TOME = new TomeItem(net.minecraft.world.item.Items.DIAMOND_BOOTS, EnchantmentCategory.ARMOR_FEET);

        public static final TomeItem BOW_TOME = new TomeItem(net.minecraft.world.item.Items.BOW, EnchantmentCategory.BOW);

        public static final TomeItem CHESTPLATE_TOME = new TomeItem(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, EnchantmentCategory.ARMOR_CHEST);

        public static final BlockItem CRYSTAL_SEASHELF = new BlockItem(Blocks.CRYSTAL_SEASHELF, new Item.Properties());

        public static final GlowyBlockItem DEEPSHELF = new GlowyBlockItem(Blocks.DEEPSHELF, new Item.Properties());

        public static final BlockItem DORMANT_DEEPSHELF = new BlockItem(Blocks.DORMANT_DEEPSHELF, new Item.Properties());

        public static final BlockItem DRACONIC_ENDSHELF = new BlockItem(Blocks.DRACONIC_ENDSHELF, new Item.Properties());

        public static final BlockItem ECHOING_DEEPSHELF = new BlockItem(Blocks.ECHOING_DEEPSHELF, new Item.Properties());

        public static final BlockItem ECHOING_SCULKSHELF = new BlockItem(Blocks.ECHOING_SCULKSHELF, new Item.Properties());

        public static final BlockItem ENDER_LIBRARY = new BlockItem(Blocks.ENDER_LIBRARY, new Item.Properties());

        public static final BlockItem ENDSHELF = new BlockItem(Blocks.ENDSHELF, new Item.Properties());

        public static final ExtractionTomeItem EXTRACTION_TOME = new ExtractionTomeItem();

        public static final TomeItem FISHING_TOME = new TomeItem(net.minecraft.world.item.Items.FISHING_ROD, EnchantmentCategory.FISHING_ROD);

        public static final BlockItem GLOWING_HELLSHELF = new BlockItem(Blocks.GLOWING_HELLSHELF, new Item.Properties());

        public static final BlockItem HEART_SEASHELF = new BlockItem(Blocks.HEART_SEASHELF, new Item.Properties());

        public static final BlockItem HELLSHELF = new BlockItem(Blocks.HELLSHELF, new Item.Properties());

        public static final TomeItem HELMET_TOME = new TomeItem(net.minecraft.world.item.Items.DIAMOND_HELMET, EnchantmentCategory.ARMOR_HEAD);

        public static final ImprovedScrappingTomeItem IMPROVED_SCRAP_TOME = new ImprovedScrappingTomeItem();

        public static final Item INERT_TRIDENT = new Item(new Item.Properties().stacksTo(1));

        public static final Item INFUSED_BREATH = new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC));

        public static final GlowyBlockItem INFUSED_HELLSHELF = new GlowyBlockItem(Blocks.INFUSED_HELLSHELF, new Item.Properties());

        public static final GlowyBlockItem INFUSED_SEASHELF = new GlowyBlockItem(Blocks.INFUSED_SEASHELF, new Item.Properties());

        public static final TomeItem LEGGINGS_TOME = new TomeItem(net.minecraft.world.item.Items.DIAMOND_LEGGINGS, EnchantmentCategory.ARMOR_LEGS);

        public static final BlockItem LIBRARY = new BlockItem(Blocks.LIBRARY, new Item.Properties());

        public static final BlockItem MELONSHELF = new BlockItem(Blocks.MELONSHELF, new Item.Properties());

        public static final TomeItem OTHER_TOME = new TomeItem(net.minecraft.world.item.Items.AIR, null);

        public static final BlockItem PEARL_ENDSHELF = new BlockItem(Blocks.PEARL_ENDSHELF, new Item.Properties());

        public static final TomeItem PICKAXE_TOME = new TomeItem(net.minecraft.world.item.Items.DIAMOND_PICKAXE, EnchantmentCategory.DIGGER);

        public static final Item PRISMATIC_WEB = new Item(new Item.Properties());

        public static final BlockItem RECTIFIER = new BlockItem(Blocks.RECTIFIER, new Item.Properties());

        public static final BlockItem RECTIFIER_T2 = new BlockItem(Blocks.RECTIFIER_T2, new Item.Properties());

        public static final BlockItem RECTIFIER_T3 = new BlockItem(Blocks.RECTIFIER_T3, new Item.Properties());

        public static final ScrappingTomeItem SCRAP_TOME =  new ScrappingTomeItem();

        public static final BlockItem SEASHELF = new BlockItem(Blocks.SEASHELF, new Item.Properties());

        public static final BlockItem SIGHTSHELF = new BlockItem(Blocks.SIGHTSHELF, new Item.Properties());

        public static final BlockItem SIGHTSHELF_T2 = new BlockItem(Blocks.SIGHTSHELF_T2, new Item.Properties()); //"sightshelf_t2"

        public static final BlockItem SOUL_TOUCHED_DEEPSHELF = new BlockItem(Blocks.SOUL_TOUCHED_DEEPSHELF, new Item.Properties());

        public static final BlockItem SOUL_TOUCHED_SCULKSHELF = new BlockItem(Blocks.SOUL_TOUCHED_SCULKSHELF, new Item.Properties());

        public static final BlockItem STONESHELF = new BlockItem(Blocks.STONESHELF, new Item.Properties());

        public static final Item WARDEN_TENDRIL = new Item(new Item.Properties());

        public static final TomeItem WEAPON_TOME = new TomeItem(net.minecraft.world.item.Items.DIAMOND_SWORD, EnchantmentCategory.WEAPON);

        public static final BlockItem FILTERING_SHELF = new BlockItem(Ench.Blocks.FILTERING_SHELF, new Item.Properties().rarity(Rarity.UNCOMMON));

        public static final BlockItem TREASURE_SHELF = new BlockItem(Ench.Blocks.TREASURE_SHELF, new Item.Properties().rarity(Rarity.UNCOMMON));


        private static void init() {
            reg(BEESHELF, "beeshelf");
            reg(BLAZING_HELLSHELF, "blazing_hellshelf");
            reg(BOOTS_TOME, "boots_tome");
            reg(BOW_TOME, "bow_tome");
            reg(CHESTPLATE_TOME, "chestplate_tome");
            reg(CRYSTAL_SEASHELF, "crystal_seashelf");
            reg(DEEPSHELF, "deepshelf");
            reg(DORMANT_DEEPSHELF, "dormant_deepshelf");
            reg(DRACONIC_ENDSHELF, "draconic_endshelf");
            reg(ECHOING_DEEPSHELF, "echoing_deepshelf");
            reg(ECHOING_SCULKSHELF, "echoing_sculkshelf");
            reg(ENDER_LIBRARY, "ender_library");
            reg(ENDSHELF, "endshelf");
            reg(EXTRACTION_TOME, "extraction_tome");
            reg(FISHING_TOME, "fishing_tome");
            reg(GLOWING_HELLSHELF, "glowing_hellshelf");
            reg(HEART_SEASHELF, "heart_seashelf");
            reg(HELLSHELF, "hellshelf");
            reg(HELMET_TOME, "helmet_tome");
            reg(IMPROVED_SCRAP_TOME, "improved_scrap_tome");
            reg(INERT_TRIDENT, "inert_trident");
            reg(INFUSED_BREATH, "infused_breath");
            reg(INFUSED_HELLSHELF, "infused_hellshelf");
            reg(INFUSED_SEASHELF, "infused_seashelf");
            reg(LEGGINGS_TOME, "leggings_tome");
            reg(LIBRARY, "library");
            reg(MELONSHELF, "melonshelf");
            reg(OTHER_TOME, "other_tome");
            reg(PEARL_ENDSHELF, "pearl_endshelf");
            reg(PICKAXE_TOME, "pickaxe_tome");
            reg(PRISMATIC_WEB, "prismatic_web");
            reg(RECTIFIER, "rectifier");
            reg(RECTIFIER_T2, "rectifier_t2");
            reg(RECTIFIER_T3, "rectifier_t3");
            reg(SCRAP_TOME, "scrap_tome");
            reg(SEASHELF, "seashelf");
            reg(SIGHTSHELF, "sightshelf");
            reg(SIGHTSHELF_T2, "sightshelf_t2");
            reg(SOUL_TOUCHED_DEEPSHELF, "soul_touched_deepshelf");
            reg(SOUL_TOUCHED_SCULKSHELF, "soul_touched_sculkshelf");
            reg(STONESHELF, "stoneshelf");
            reg(WARDEN_TENDRIL, "warden_tendril");
            reg(WEAPON_TOME, "weapon_tome");
            reg(FILTERING_SHELF, "filtering_shelf");
            reg(TREASURE_SHELF, "treasure_shelf");
        }

        private static void reg(Item item, String id){
            Registry.register(BuiltInRegistries.ITEM, Apotheosis.loc(id), item);
        }
    }

    public static final class Enchantments {

        public static final BerserkersFuryEnchant BERSERKERS_FURY = new BerserkersFuryEnchant();

        public static final BaneEnchant BANE_OF_ILLAGERS = new BaneEnchant(Enchantment.Rarity.UNCOMMON, MobType.ILLAGER, EquipmentSlot.MAINHAND);

        public static final ChainsawEnchant CHAINSAW = new ChainsawEnchant();

        public static final ChromaticEnchant CHROMATIC = new ChromaticEnchant();

        public static final CrescendoEnchant CRESCENDO = new CrescendoEnchant();

        public static final EarthsBoonEnchant EARTHS_BOON = new EarthsBoonEnchant();

        public static final EndlessQuiverEnchant ENDLESS_QUIVER = new EndlessQuiverEnchant();

        public static final ExploitationEnchant EXPLOITATION = new ExploitationEnchant();

        public static final GrowthSerumEnchant GROWTH_SERUM = new GrowthSerumEnchant();

        public static final IcyThornsEnchant ICY_THORNS = new IcyThornsEnchant();

        public static final InertEnchantment INFUSION = new InertEnchantment();

        public static final KnowledgeEnchant KNOWLEDGE = new KnowledgeEnchant();

        public static final LifeMendingEnchant LIFE_MENDING = new LifeMendingEnchant();

        public static final MinersFervorEnchant MINERS_FERVOR = new MinersFervorEnchant();

        public static final NaturesBlessingEnchant NATURES_BLESSING = new NaturesBlessingEnchant();

        public static final ObliterationEnchant OBLITERATION = new ObliterationEnchant();

        public static final ReboundingEnchant REBOUNDING = new ReboundingEnchant();

        public static final ReflectiveEnchant REFLECTIVE = new ReflectiveEnchant();

        public static final ScavengerEnchant SCAVENGER = new ScavengerEnchant();

        public static final ShieldBashEnchant SHIELD_BASH = new ShieldBashEnchant();

        public static final SpearfishingEnchant SPEARFISHING = new SpearfishingEnchant();

        public static final SplittingEnchant SPLITTING = new SplittingEnchant();

        public static final StableFootingEnchant STABLE_FOOTING = new StableFootingEnchant();

        public static final TemptingEnchant TEMPTING = new TemptingEnchant();

        private static void init() {
            reg(BERSERKERS_FURY, "berserkers_fury");
            reg(CHAINSAW, "chainsaw");
            reg(CHROMATIC, "chromatic");
            reg(CRESCENDO, "crescendo");
            reg(EARTHS_BOON, "earths_boon");
            reg(ENDLESS_QUIVER, "endless_quiver");
            reg(EXPLOITATION, "exploitation");
            reg(GROWTH_SERUM, "growth_serum");
            reg(ICY_THORNS, "icy_thorns");
            reg(INFUSION, "infusion");
            reg(KNOWLEDGE, "knowledge");
            reg(LIFE_MENDING, "life_mending");
            reg(MINERS_FERVOR, "miners_fervor");
            reg(NATURES_BLESSING, "natures_blessing");
            reg(OBLITERATION, "obliteration");
            reg(REBOUNDING, "rebounding");
            reg(REFLECTIVE, "reflective");
            reg(SCAVENGER, "scavenger");
            reg(SHIELD_BASH, "shield_bash");
            reg(SPEARFISHING, "spearfishing");
            reg(SPLITTING, "splitting");
            reg(STABLE_FOOTING, "stable_footing");
            reg(TEMPTING, "tempting");
            reg(BANE_OF_ILLAGERS, "bane_of_illagers");
        }
        private static void reg(Enchantment ench, String id){
            Registry.register(BuiltInRegistries.ENCHANTMENT, Apotheosis.loc(id), ench);
        }
    }

    public static class Tabs {

        public static final ResourceKey<CreativeModeTab> ENCH = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Apotheosis.loc("ench"));
        public static final CreativeModeTab ENCHTAB = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.zenith.ench"))
                .icon(Items.HELLSHELF::getDefaultInstance)
                .displayItems((a,b) -> {
                    Apoth.fill(b, Items.HELLSHELF, Items.INFUSED_HELLSHELF, Items.BLAZING_HELLSHELF, Items.GLOWING_HELLSHELF, Items.SEASHELF, Items.INFUSED_SEASHELF,
                            Items.CRYSTAL_SEASHELF, Items.HEART_SEASHELF, Items.DORMANT_DEEPSHELF, Items.DEEPSHELF, Items.ECHOING_DEEPSHELF, Items.SOUL_TOUCHED_DEEPSHELF, Items.ECHOING_SCULKSHELF,
                            Items.SOUL_TOUCHED_SCULKSHELF, Items.ENDSHELF, Items.PEARL_ENDSHELF, Items.DRACONIC_ENDSHELF, Items.BEESHELF, Items.MELONSHELF, Items.STONESHELF, Items.RECTIFIER,
                            Items.RECTIFIER_T2, Items.RECTIFIER_T3, Items.SIGHTSHELF, Items.SIGHTSHELF_T2, Items.FILTERING_SHELF, Items.TREASURE_SHELF,  Items.LIBRARY, Items.ENDER_LIBRARY);

                    Apoth.fill(b, Items.HELMET_TOME, Items.CHESTPLATE_TOME, Items.LEGGINGS_TOME, Items.BOOTS_TOME, Items.WEAPON_TOME, Items.BOW_TOME, Items.PICKAXE_TOME,
                            Items.FISHING_TOME, Items.OTHER_TOME, Items.SCRAP_TOME, Items.IMPROVED_SCRAP_TOME, Items.EXTRACTION_TOME);

                    Apoth.fill(b, Items.PRISMATIC_WEB, Items.INERT_TRIDENT, Items.WARDEN_TENDRIL, Items.INFUSED_BREATH);

                    Apoth.fill(b, Enchantments.BERSERKERS_FURY, Enchantments.CHAINSAW, Enchantments.CHROMATIC, Enchantments.CRESCENDO, Enchantments.EARTHS_BOON, Enchantments.ENDLESS_QUIVER, Enchantments.EXPLOITATION,
                            Enchantments.GROWTH_SERUM, Enchantments.ICY_THORNS, Enchantments.KNOWLEDGE, Enchantments.LIFE_MENDING, Enchantments.MINERS_FERVOR, Enchantments.NATURES_BLESSING, Enchantments.OBLITERATION, Enchantments.REBOUNDING,
                            Enchantments.REFLECTIVE, Enchantments.SCAVENGER, Enchantments.SHIELD_BASH, Enchantments.SPEARFISHING, Enchantments.SPLITTING, Enchantments.STABLE_FOOTING, Enchantments.TEMPTING);
                })
                .build();



        private static void bootstrap() {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ENCH, ENCHTAB);
        }

    }

    public static final class Particles {
        public static final SimpleParticleType ENCHANT_FIRE = FabricParticleTypes.simple();
        public static final SimpleParticleType ENCHANT_WATER = FabricParticleTypes.simple();
        public static final SimpleParticleType ENCHANT_SCULK = FabricParticleTypes.simple();
        public static final SimpleParticleType ENCHANT_END = FabricParticleTypes.simple();
    }

    public static final class Tiles {
        public static final BlockEntityType<BasicLibraryTile> LIBRARY = Apoth.registerBEType("library", new BlockEntityType<>(EnchLibraryTile.BasicLibraryTile::new, ImmutableSet.of(Ench.Blocks.LIBRARY), null));
        public static final BlockEntityType<EnchLibraryTile.EnderLibraryTile> ENDER_LIBRARY = Apoth.registerBEType("ender_library", new BlockEntityType<>(EnchLibraryTile.EnderLibraryTile::new, ImmutableSet.of(Ench.Blocks.ENDER_LIBRARY), null));
        public static final BlockEntityType<FilteringShelfBlock.FilteringShelfTile> FILTERING_SHELF = Apoth.registerBEType("filtering_shelf", new BlockEntityType<>(FilteringShelfBlock.FilteringShelfTile::new, ImmutableSet.of(Blocks.FILTERING_SHELF), null));

        private static void bootstrap() {}
    }

    public static final class RecipeTypes {
        public static final RecipeType<EnchantingRecipe> INFUSION = PlaceboUtil.makeRecipeType("zenith:enchanting");
        private static void bootstrap() {}
    }

    public static final class Menus {

        public static final MenuType<EnchLibraryContainer> LIBRARY = Apoth.registerMenu("library", new ExtendedScreenHandlerType<>(EnchLibraryContainer::new));
        public static final MenuType<ApothEnchantmentMenu> ENCHANTING_TABLE = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("enchanting_table"), ApothEnchantmentMenu::new);

        private static void bootstrap() {}
    }
    public static void bootstrap() {
        Blocks.init();
        Items.init();
        Enchantments.init();
        Tabs.bootstrap();
        Apoth.Menus.bootstrap();
        Tiles.bootstrap();
        RecipeTypes.bootstrap();
        Menus.bootstrap();
    }

}
