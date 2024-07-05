package dev.shadowsoffire.apotheosis.adventure;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Blocks;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.adventure.boss.BossEvents;
import dev.shadowsoffire.apotheosis.adventure.boss.BossRegistry;
import dev.shadowsoffire.apotheosis.adventure.boss.Exclusion;
import dev.shadowsoffire.apotheosis.adventure.boss.MinibossRegistry;
import dev.shadowsoffire.apotheosis.adventure.loot.*;
import dev.shadowsoffire.apotheosis.adventure.net.RadialStateChangeMessage;
import dev.shadowsoffire.apotheosis.adventure.socket.AddSocketsRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.UnnamingRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.WithdrawalRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
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

    public static void init() {
        reload(false);
        AdventureEvents.init();
        Adventure.bootstrap();
        BossEvents.INSTANCE.init();
        RarityRegistry.INSTANCE.register();
        AffixRegistry.INSTANCE.register();
        GemRegistry.INSTANCE.register();
        AffixLootRegistry.INSTANCE.register();
        BossRegistry.INSTANCE.register();
        RogueSpawnerRegistry.INSTANCE.register();
        MinibossRegistry.INSTANCE.register();

        //    if (FabricLoader.getInstance().isModLoaded("gateways")) GatewaysCompat.register();
        //TODO Add support for the gateways port
        Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, Apotheosis.loc("random_affix_item"), AffixLootPoolEntry.TYPE);
        Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, Apotheosis.loc("random_gem"), GemLootPoolEntry.TYPE);
        Exclusion.initSerializers();
        GemBonus.initCodecs();
        RadialStateChangeMessage.init();
        CustomIngredientSerializer.register(GemIngredient.Serializer.INSTANCE);
        CustomIngredientSerializer.register(AffixItemIngredient.Serializer.INSTANCE);

        structureDatapack();
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
        Apoth.registerBlock(Blocks.AUGMENTING_TABLE, "augmenting_table");
    }

    public static void items() {
        Apoth.registerItem(Adventure.Items.COMMON_MATERIAL, "common_material");
        Apoth.registerItem(Adventure.Items.UNCOMMON_MATERIAL, "uncommon_material");
        Apoth.registerItem(Adventure.Items.RARE_MATERIAL, "rare_material");
        Apoth.registerItem(Adventure.Items.EPIC_MATERIAL, "epic_material");
        Apoth.registerItem(Adventure.Items.MYTHIC_MATERIAL, "mythic_material");
        Apoth.registerItem(Adventure.Items.ANCIENT_MATERIAL, "ancient_material");
        Apoth.registerItem(Adventure.Items.GEM_DUST, "gem_dust");
        Apoth.registerItem(Adventure.Items.GEM_FUSED_SLATE, "gem_fused_slate");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_SOCKETING, "sigil_of_socketing");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_WITHDRAWAL, "sigil_of_withdrawal");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_REBIRTH, "sigil_of_rebirth");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_ENHANCEMENT, "sigil_of_enhancement");
        Apoth.registerItem(Adventure.Items.SIGIL_OF_UNNAMING, "sigil_of_unnaming");
        Apoth.registerItem(Adventure.Items.BOSS_SUMMONER, "boss_summoner");
        Apoth.registerItem(Adventure.Items.SIMPLE_REFORGING_TABLE, "simple_reforging_table");
        Apoth.registerItem(Adventure.Items.REFORGING_TABLE, "reforging_table");
        Apoth.registerItem(Adventure.Items.SALVAGING_TABLE, "salvaging_table");
        Apoth.registerItem(Adventure.Items.GEM_CUTTING_TABLE, "gem_cutting_table");
        Apoth.registerItem(Adventure.Items.AUGMENTING_TABLE, "augmenting_table");
        Apoth.registerItem(Adventure.Items.GEM, "gem");
    }

    public static void tiles() {
    //    Apoth.registerBEType("boss_spawner", new TickingBlockEntityType<>(BossSpawnerTile::new, ImmutableSet.of(Blocks.BOSS_SPAWNER), false, true));
    //    Apoth.registerBEType("reforging_table", new TickingBlockEntityType<>(ReforgingTableTile::new, ImmutableSet.of(Blocks.SIMPLE_REFORGING_TABLE, Blocks.REFORGING_TABLE), true, false));
    //    Apoth.registerBEType("salvaging_table", new BlockEntityType<>(SalvagingTableTile::new, ImmutableSet.of(Blocks.SALVAGING_TABLE), null));
    }

    public static void serializers() {
        Apoth.registerSerializer("socketing", SocketingRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("withdrawal", WithdrawalRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("unnaming", UnnamingRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("add_sockets", AddSocketsRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("salvaging", SalvagingRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("reforging", ReforgingRecipe.Serializer.INSTANCE);
    }

    public static void miscRegistration() {
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("gems"), GemLootModifier.CODEC);
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("affix_loot"), AffixLootModifier.CODEC);
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("affix_conversion"), AffixConvertLootModifier.CODEC);
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Apotheosis.loc("affix_hook"), AffixHookLootModifier.CODEC);
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
        public boolean isBaseIngredient(ItemStack stack) {
            return !LootCategory.forItem(stack).isNone();
        }
    }

}
