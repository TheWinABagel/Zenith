package dev.shadowsoffire.apotheosis.adventure;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Blocks;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.*;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.boss.*;
import dev.shadowsoffire.apotheosis.adventure.loot.*;
import dev.shadowsoffire.apotheosis.adventure.spawner.RogueSpawnerRegistry;
import dev.shadowsoffire.apotheosis.util.AffixItemIngredient;
import dev.shadowsoffire.apotheosis.util.GemIngredient;
import dev.shadowsoffire.apotheosis.util.NameHelper;
import dev.shadowsoffire.placebo.config.Configuration;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class AdventureModule {

    public static final Logger LOGGER = LogManager.getLogger("Zenith : Adventure");
    public static final boolean STAGES_LOADED = FabricLoader.getInstance().isModLoaded("gamestages");



    public AdventureModule() {
        Adventure.bootstrap();
    }

    public void preInit() {
    //    ObfuscationReflectionHelper.setPrivateValue(RangedAttribute.class, (RangedAttribute) Attributes.ARMOR, 200D, "f_22308_");
    //    ObfuscationReflectionHelper.setPrivateValue(RangedAttribute.class, (RangedAttribute) Attributes.ARMOR_TOUGHNESS, 100D, "f_22308_");
    }

    public static void init() {
        reload(false);
        AdventureEvents.init();
        Adventure.bootstrap();
        BossEvents.init();
        RarityRegistry.INSTANCE.register();
        AffixRegistry.INSTANCE.register();
        GemRegistry.INSTANCE.register();
        AffixLootRegistry.INSTANCE.register();
        BossRegistry.INSTANCE.register();
        RogueSpawnerRegistry.INSTANCE.register();
        MinibossRegistry.INSTANCE.register();
        structureDatapack();

        //    if (FabricLoader.getInstance().isModLoaded("gateways")) GatewaysCompat.register();
        //    if (FabricLoader.getInstance().isModLoaded("theoneprobe")) AdventureTOPPlugin.register();
        //    if (FabricLoader.getInstance().isModLoaded("twilightforest")) AdventureTwilightCompat.register();
            //LootSystem.defaultBlockTable(Blocks.SIMPLE_REFORGING_TABLE);
            //LootSystem.defaultBlockTable(Blocks.REFORGING_TABLE);
            //LootSystem.defaultBlockTable(Blocks.SALVAGING_TABLE);
            //LootSystem.defaultBlockTable(Blocks.GEM_CUTTING_TABLE);
            Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(Apotheosis.MODID, "random_affix_item"), AffixLootPoolEntry.TYPE);
            Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(Apotheosis.MODID, "random_gem"), GemLootPoolEntry.TYPE);
            Exclusion.initSerializers();
            GemBonus.initCodecs();
        CustomIngredientSerializer.register(GemIngredient.Serializer.INSTANCE);
        CustomIngredientSerializer.register(AffixItemIngredient.Serializer.INSTANCE);
            //CraftingHelper.register(Apotheosis.loc("affix_item"), AffixItemIngredient.Serializer.INSTANCE);
        //    CraftingHelper.register(Apotheosis.loc("gem"), GemIngredient.Serializer.INSTANCE);
/*
            TabFillingRegistry.register(Adventure.Tabs.ADVENTURE.getKey(), Items.COMMON_MATERIAL, Items.UNCOMMON_MATERIAL, Items.RARE_MATERIAL, Items.EPIC_MATERIAL, Items.MYTHIC_MATERIAL, Items.GEM_DUST, Items.VIAL_OF_EXPULSION,
                Items.VIAL_OF_EXTRACTION, Items.VIAL_OF_UNNAMING, Items.SIGIL_OF_SOCKETING, Items.SIGIL_OF_ENHANCEMENT, Items.SUPERIOR_SIGIL_OF_SOCKETING, Items.SUPERIOR_SIGIL_OF_ENHANCEMENT, Items.BOSS_SUMMONER,
                Items.SIMPLE_REFORGING_TABLE, Items.REFORGING_TABLE, Items.SALVAGING_TABLE, Items.GEM_CUTTING_TABLE);
            TabFillingRegistry.register(Adventure.Tabs.ADVENTURE.getKey(), Items.GEM);*/
        tiles();
        serializers();
        blocks();
        items();
        miscRegistration();
    }

    public static void blocks() {
        Apoth.registerBlock(Blocks.REFORGING_TABLE, "reforging_table");
        Apoth.registerBlock(Blocks.BOSS_SPAWNER, "boss_spawner");
        Apoth.registerBlock(Blocks.GEM_CUTTING_TABLE, "gem_cutting_table");
        Apoth.registerBlock(Blocks.SIMPLE_REFORGING_TABLE, "simple_reforging_table");
        Apoth.registerBlock(Blocks.SALVAGING_TABLE, "salvaging_table");
    }

    public static void items() {
        Apoth.registerItem(Adventure.Items.COMMON_MATERIAL, "common_material");
        Apoth.registerItem(Adventure.Items.UNCOMMON_MATERIAL, "uncommon_material");
        Apoth.registerItem(Adventure.Items.RARE_MATERIAL, "rare_material");
        Apoth.registerItem(Adventure.Items.EPIC_MATERIAL, "epic_material");
        Apoth.registerItem(Adventure.Items.MYTHIC_MATERIAL, "mythic_material");
        Apoth.registerItem(Adventure.Items.ANCIENT_MATERIAL, "ancient_material");
        Apoth.registerItem(Adventure.Items.GEM_DUST, "gem_dust");
        Apoth.registerItem(Adventure.Items.VIAL_OF_EXPULSION, "vial_of_expulsion");
        Apoth.registerItem(Adventure.Items.VIAL_OF_EXTRACTION, "vial_of_extraction");
        Apoth.registerItem(Adventure.Items.VIAL_OF_UNNAMING, "vial_of_unnaming");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_SOCKETING, "sigil_of_socketing");
        Apoth.registerItem(Adventure.Items.SUPERIOR_SIGIL_OF_SOCKETING, "superior_sigil_of_socketing");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_ENHANCEMENT, "sigil_of_enhancement");
        Apoth.registerItem(Adventure.Items.SUPERIOR_SIGIL_OF_ENHANCEMENT, "superior_sigil_of_enhancement");
        Apoth.registerItem(Adventure.Items.BOSS_SUMMONER, "boss_summoner");
        Apoth.registerItem(Adventure.Items.SIMPLE_REFORGING_TABLE, "simple_reforging_table");
        Apoth.registerItem(Adventure.Items.REFORGING_TABLE, "reforging_table");
        Apoth.registerItem(Adventure.Items.SALVAGING_TABLE, "salvaging_table");
        Apoth.registerItem(Adventure.Items.GEM_CUTTING_TABLE, "gem_cutting_table");
        Apoth.registerItem(Adventure.Items.GEM, "gem");
    }

    public static void tiles() {
    //    Apoth.registerBEType("boss_spawner", new TickingBlockEntityType<>(BossSpawnerTile::new, ImmutableSet.of(Blocks.BOSS_SPAWNER), false, true));
    //    Apoth.registerBEType("reforging_table", new TickingBlockEntityType<>(ReforgingTableTile::new, ImmutableSet.of(Blocks.SIMPLE_REFORGING_TABLE, Blocks.REFORGING_TABLE), true, false));
    //    Apoth.registerBEType("salvaging_table", new BlockEntityType<>(SalvagingTableTile::new, ImmutableSet.of(Blocks.SALVAGING_TABLE), null));
    }

    public static void serializers() {
        Apoth.registerSerializer("socketing", SocketingRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("expulsion", ExpulsionRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("extraction", ExtractionRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("unnaming", UnnamingRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("add_sockets", AddSocketsRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("salvaging", SalvagingRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("reforging", ReforgingRecipe.Serializer.INSTANCE);
    }

    public static void miscRegistration() {
        LOGGER.info("Registering Zenith loot modifiers");

        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("gems"), GemLootModifier.CODEC);
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("affix_loot"), AffixLootModifier.CODEC);
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("affix_conversion"), AffixConvertLootModifier.CODEC);
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("affix_hook"), AffixHookLootModifier.CODEC);

    /*    if (e.getForgeRegistry() == (Object) ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get()) {
            e.getForgeRegistry().register("blacklist", BlacklistModifier.CODEC);
        }*/

    }

    public static void structureDatapack() {
        ResourceLocation id = Apotheosis.loc("structures");
        ModContainer container = getModContainer(id);
        ResourceManagerHelper.registerBuiltinResourcePack(id, container,  ResourcePackActivationType.DEFAULT_ENABLED);
    }

    private static ModContainer getModContainer(ResourceLocation pack) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                if (mod.findPath("resourcepacks/" + pack.getPath()).isPresent()) {
                    LOGGER.info("LOADING DEV ENVIRONMENT DATAPACK");
                    return mod;
                }
            }
        }
        return FabricLoader.getInstance().getModContainer(pack.getNamespace()).orElseThrow();
    }

    /**
     * Loads all configurable data for the adventure module.
     */
    public static void reload(boolean e) {
        Configuration mainConfig = new Configuration(new File(Apotheosis.configDir, "adventure.cfg"));
        Configuration nameConfig = new Configuration(new File(Apotheosis.configDir, "names.cfg"));
        AdventureConfig.load(mainConfig);
        NameHelper.load(nameConfig);
        if (!e && mainConfig.hasChanged()) mainConfig.save();
        if (!e && nameConfig.hasChanged()) nameConfig.save();
    }

    public static void debugLog(BlockPos pos, String name) {
        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Generated a {} at {} {} {}", name, pos.getX(), pos.getY(), pos.getZ());
    }


    public static class ApothSmithingRecipe extends SmithingTransformRecipe {

        public static final int TEMPLATE = 0, BASE = 1, ADDITION = 2;

        public ApothSmithingRecipe(ResourceLocation pId, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
            super(pId, Ingredient.EMPTY, pBase, pAddition, pResult);
        }

        @Override
        public boolean isBaseIngredient(ItemStack pStack) {
            return !LootCategory.forItem(pStack).isNone();
        }
    }

}
