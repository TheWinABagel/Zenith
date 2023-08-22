package dev.shadowsoffire.apotheosis.ench;

import dev.shadowsoffire.apotheosis.Apoth.Particles;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.Apotheosis.ModularDeferredHelper;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile.BasicLibraryTile;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile.EnderLibraryTile;
import dev.shadowsoffire.apotheosis.ench.objects.TypedShelfBlock.SculkShelfBlock;
import dev.shadowsoffire.apotheosis.ench.anvil.ObliterationEnchant;
import dev.shadowsoffire.apotheosis.ench.anvil.SplittingEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.*;
import dev.shadowsoffire.apotheosis.ench.enchantments.corrupted.BerserkersFuryEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.corrupted.LifeMendingEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.*;
import dev.shadowsoffire.apotheosis.ench.enchantments.twisted.ExploitationEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.twisted.MinersFervorEnchant;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryBlock;
import dev.shadowsoffire.apotheosis.ench.objects.*;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class Ench {

    public static final class Blocks {

        public static final Block BEESHELF = woodShelf("beeshelf", MapColor.COLOR_YELLOW, 0.75F, () -> ParticleTypes.ENCHANT);

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

        public static final Block MELONSHELF = woodShelf("melonshelf", MapColor.COLOR_GREEN, 0.75F, () -> ParticleTypes.ENCHANT);

        public static final Block PEARL_ENDSHELF = stoneShelf("pearl_endshelf", MapColor.SAND, 4.5F, Particles.ENCHANT_END);

        public static final Block RECTIFIER = stoneShelf("rectifier", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Block RECTIFIER_T2 = stoneShelf("rectifier_t2", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block RECTIFIER_T3 = stoneShelf("rectifier_t3", MapColor.SAND, 1.5F, Particles.ENCHANT_END);

        public static final Block SEASHELF = stoneShelf("seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Block SIGHTSHELF = stoneShelf("sightshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block SIGHTSHELF_T2 = stoneShelf("sightshelf_t2", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Block SOUL_TOUCHED_DEEPSHELF = stoneShelf("soul_touched_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Block SOUL_TOUCHED_SCULKSHELF = sculkShelf("soul_touched_sculkshelf");

        public static final Block STONESHELF = stoneShelf("stoneshelf", MapColor.STONE, 1.25F, () -> ParticleTypes.ENCHANT);

        private static void bootstrap() {}

        private static Block sculkShelf(String id) {
            return  new SculkShelfBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).randomTicks().requiresCorrectToolForDrops().strength(3.5F), Particles.ENCHANT_SCULK);
        }

        private static Block stoneShelf(String id, MapColor color, float strength, Supplier<? extends ParticleOptions> particle) {
            return new TypedShelfBlock(Block.Properties.of().requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(color).strength(strength), particle);
        }

        private static Block woodShelf(String id, MapColor color, float strength, Supplier<? extends ParticleOptions> particle) {
            return  new TypedShelfBlock(Block.Properties.of().sound(SoundType.WOOD).mapColor(color).strength(strength), particle);
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

        private static void bootstrap() {}
        /*
        private Item register(){
            return new
        }*/
    }

    public static final class Enchantments {

        public static final BerserkersFuryEnchant BERSERKERS_FURY = new BerserkersFuryEnchant();

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

        private static void bootstrap() {}

    }

    public static class Tabs {

    //    public static final CreativeModeTab ENCH = R.tab("ench",
    //        () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.apotheosis.ench")).icon(() -> Items.HELLSHELF.getDefaultInstance()).withTabsBefore(Apotheosis.loc("adventure")).build());

        private static void bootstrap() {}

    }

    private static final DeferredHelper R = ModularDeferredHelper.create(() -> Apotheosis.enableEnch);

    public static void bootstrap() {
        Blocks.bootstrap();
        Items.bootstrap();
        Enchantments.bootstrap();
        Tabs.bootstrap();
    }

}
