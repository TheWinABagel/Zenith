package dev.shadowsoffire.apotheosis;


import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.*;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTile;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingTableTile;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketAffix;
import dev.shadowsoffire.apotheosis.adventure.boss.BossSpawnerBlock;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryContainer;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile;
import dev.shadowsoffire.apotheosis.ench.objects.GlowyBlockItem;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantTile;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.garden.EnderLeadItem;

//import dev.shadowsoffire.apotheosis.village.fletching.arrows.*;
import dev.shadowsoffire.apotheosis.potion.PotionCharmItem;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.spawner.ApothSpawnerBlock;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingContainer;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.*;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.registry.RegObjHelper;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Object Holder Class. For the main mod class, see {@link Apotheosis}
 */
public class Apoth {

    public static final RegObjHelper R = new RegObjHelper(Apotheosis.MODID);

    public static final class Items {
        public static final PotionCharmItem POTION_CHARM = new PotionCharmItem();
        public static final Item LUCKY_FOOT = new GlowyBlockItem.GlowyItem(new Item.Properties());
        public static final Item OBSIDIAN_ARROW = new ObsidianArrowItem(new Item.Properties());
        public static final Item BROADHEAD_ARROW = new BroadheadArrowItem(new Item.Properties());
        public static final Item EXPLOSIVE_ARROW = new ExplosiveArrowItem(new Item.Properties());
        public static final Item IRON_MINING_ARROW = new MiningArrowItem(() -> net.minecraft.world.item.Items.IRON_PICKAXE, MiningArrowEntity.Type.IRON);
        public static final Item DIAMOND_MINING_ARROW = new MiningArrowItem(() -> net.minecraft.world.item.Items.DIAMOND_PICKAXE, MiningArrowEntity.Type.DIAMOND);
        public static final Item ENDER_LEAD = new EnderLeadItem();
        public static final RegistryObject<Item> SKULL_FRAGMENT = new RegistryObject(new ResourceLocation("wstweaks", "fragment"), Registries.ITEM);
    }

    public static final class Enchantments {
        public static Enchantment CAPTURING = new CapturingEnchant();
    }

    public static final class Potions {
        public static final Potion RESISTANCE = registerPot(new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600)), "resistance");
        public static final Potion LONG_RESISTANCE = registerPot(new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 9600)), "long_resistance");
        public static final Potion STRONG_RESISTANCE = registerPot(new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1)), "strong_resistance");
        public static final Potion ABSORPTION = registerPot(new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1)), "absorption");
        public static final Potion LONG_ABSORPTION = registerPot(new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1)), "long_absorption");
        public static final Potion STRONG_ABSORPTION = registerPot(new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 600, 3)), "strong_absorption");
        public static final Potion HASTE = registerPot(new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 3600)), "haste");
        public static final Potion LONG_HASTE = registerPot(new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 9600)), "long_haste");
        public static final Potion STRONG_HASTE = registerPot(new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)), "strong_haste");
        public static final Potion FATIGUE = registerPot(new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3600)), "fatigue");
        public static final Potion LONG_FATIGUE = registerPot(new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 9600)), "long_fatigue");
        public static final Potion STRONG_FATIGUE = registerPot(new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1800, 1)), "strong_fatigue");
        public static final Potion WITHER = registerPot(new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 3600)), "wither");
        public static final Potion LONG_WITHER = registerPot(new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 9600)), "long_wither");
        public static final Potion STRONG_WITHER = registerPot(new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 1800, 1)), "strong_wither");
        public static final Potion SUNDERING = registerPot(new Potion("sundering", new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 3600)), "sundering");
        public static final Potion LONG_SUNDERING = registerPot(new Potion("sundering", new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 9600)), "long_sundering");
        public static final Potion STRONG_SUNDERING = registerPot(new Potion("sundering", new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 1800, 1)), "strong_sundering");
        public static final Potion KNOWLEDGE = registerPot(new Potion("knowledge", new MobEffectInstance(ALObjects.MobEffects.KNOWLEDGE, 2400)), "knowledge");
        public static final Potion LONG_KNOWLEDGE = registerPot(new Potion("knowledge", new MobEffectInstance(ALObjects.MobEffects.KNOWLEDGE, 4800)), "long_knowledge");
        public static final Potion STRONG_KNOWLEDGE = registerPot(new Potion("knowledge", new MobEffectInstance(ALObjects.MobEffects.KNOWLEDGE, 1200, 1)), "strong_knowledge");
        public static final Potion VITALITY = registerPot(new Potion("vitality", new MobEffectInstance(ALObjects.MobEffects.VITALITY, 4800)), "vitality");
        public static final Potion LONG_VITALITY = registerPot(new Potion("vitality", new MobEffectInstance(ALObjects.MobEffects.VITALITY, 14400)), "long_vitality");
        public static final Potion STRONG_VITALITY = registerPot(new Potion("vitality", new MobEffectInstance(ALObjects.MobEffects.VITALITY, 3600, 1)), "strong_vitality");
        public static final Potion GRIEVOUS = registerPot(new Potion("grievous", new MobEffectInstance(ALObjects.MobEffects.GRIEVOUS, 4800)), "grievous");
        public static final Potion LONG_GRIEVOUS = registerPot(new Potion("grievous", new MobEffectInstance(ALObjects.MobEffects.GRIEVOUS, 14400)), "long_grievous");
        public static final Potion STRONG_GRIEVOUS = registerPot(new Potion("grievous", new MobEffectInstance(ALObjects.MobEffects.GRIEVOUS, 3600, 1)), "strong_grievous");
        public static final Potion LEVITATION = registerPot(new Potion("levitation", new MobEffectInstance(MobEffects.LEVITATION, 2400)), "levitation");
        public static final Potion FLYING = registerPot(new Potion("flying", new MobEffectInstance(ALObjects.MobEffects.FLYING, 9600)), "flying");
        public static final Potion LONG_FLYING = registerPot(new Potion("flying", new MobEffectInstance(ALObjects.MobEffects.FLYING, 18000)), "long_flying");
        public static final Potion EXTRA_LONG_FLYING = registerPot(new Potion("flying", new MobEffectInstance(ALObjects.MobEffects.FLYING, 36000)), "extra_long_flying");
    }

    public static final class Entities {
        public static final EntityType<ObsidianArrowEntity> OBSIDIAN_ARROW = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Apotheosis.MODID, "obsidian_arrow"),
                FabricEntityTypeBuilder.<ObsidianArrowEntity>create(MobCategory.MISC, ObsidianArrowEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                        .trackRangeBlocks(4)
                        .trackedUpdateRate(20)
                        .build());
        public static final EntityType<BroadheadArrowEntity> BROADHEAD_ARROW = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Apotheosis.MODID, "broadhead_arrow"),
                FabricEntityTypeBuilder.<BroadheadArrowEntity>create(MobCategory.MISC, BroadheadArrowEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                        .trackRangeBlocks(4)
                        .trackedUpdateRate(20)
                        .build());
        public static final EntityType<ExplosiveArrowEntity> EXPLOSIVE_ARROW = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Apotheosis.MODID, "explosive_arrow"),
                FabricEntityTypeBuilder.<ExplosiveArrowEntity>create(MobCategory.MISC, ExplosiveArrowEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                        .trackRangeBlocks(4)
                        .trackedUpdateRate(20)
                        .build());
        public static final EntityType<MiningArrowEntity> MINING_ARROW = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(Apotheosis.MODID, "mining_arrow"),
                FabricEntityTypeBuilder.<MiningArrowEntity>create(MobCategory.MISC, MiningArrowEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                        .trackRangeBlocks(4)
                        .trackedUpdateRate(20)
                        .build());
    }

    public static final class Menus {
        public static void bootstrap(){}
        public static final MenuType<FletchingContainer> FLETCHING = ScreenHandlerRegistry.registerSimple(new ResourceLocation(Apotheosis.MODID, "fletching"), FletchingContainer::new);
        public static final MenuType<EnchLibraryContainer> LIBRARY = Apoth.registerMenu("library", new ExtendedScreenHandlerType<>(EnchLibraryContainer::new));
        public static final MenuType<ApothEnchantmentMenu> ENCHANTING_TABLE = ScreenHandlerRegistry.registerSimple(new ResourceLocation(Apotheosis.MODID, "enchanting_table"), ApothEnchantmentMenu::new);
    }

    public static final class Tiles {
        public static void bootstrap(){}
        public static final BlockEntityType<EnchLibraryTile.BasicLibraryTile> LIBRARY = Apoth.registerBEType("library", new BlockEntityType<>(EnchLibraryTile.BasicLibraryTile::new, ImmutableSet.of(Ench.Blocks.LIBRARY), null));
        public static final BlockEntityType<EnchLibraryTile.EnderLibraryTile> ENDER_LIBRARY = Apoth.registerBEType("ender_library", new BlockEntityType<>(EnchLibraryTile.EnderLibraryTile::new, ImmutableSet.of(Ench.Blocks.ENDER_LIBRARY), null));
        public static final BlockEntityType<AnvilTile> ANVIL_TILE = Apoth.registerBEType("anvil", new BlockEntityType<>(AnvilTile::new, ImmutableSet.of(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL), null));
        //public static final BlockEntityType<ApothEnchantTile> ENCHANT_TILE = Apoth.registerBEType("enchanting_table", new BlockEntityType<>(ApothEnchantTile::new, ImmutableSet.of(Blocks.ENCHANTING_TABLE), null));


    }




        public static final class Tags {
        public static final TagKey<Item> CUSTOM_ENCHANTABLES = registerItemTag(new ResourceLocation(Apotheosis.MODID, "custom_enchantables"));
            public static final TagKey<Item> BOON_DROPS = registerItemTag(new ResourceLocation(Apotheosis.MODID, "boon_drops"));
            public static final TagKey<Item> SPEARFISHING_DROPS = registerItemTag(new ResourceLocation(Apotheosis.MODID, "spearfishing_drops"));
        }

    public static final class RecipeTypes {
        public static final RecipeType<FletchingRecipe> FLETCHING = PlaceboUtil.makeRecipeType("apotheosis:fletching");
        public static final RecipeType<EnchantingRecipe> INFUSION = PlaceboUtil.makeRecipeType("apotheosis:enchanting");
        public static final RecipeType<SpawnerModifier> MODIFIER = PlaceboUtil.makeRecipeType("apotheosis:spawner_modifier");
        public static final RecipeType<SalvagingRecipe> SALVAGING = PlaceboUtil.makeRecipeType("apotheosis:salvaging");
        public static final RecipeType<ReforgingRecipe> REFORGING = PlaceboUtil.makeRecipeType("apotheosis:reforging");
    }

    public static final class LootTables {
        public static final ResourceLocation CHEST_VALUABLE = Apotheosis.loc("chests/chest_valuable");
        public static final ResourceLocation SPAWNER_BRUTAL_ROTATE = Apotheosis.loc("chests/spawner_brutal_rotate");
        public static final ResourceLocation SPAWNER_BRUTAL = Apotheosis.loc("chests/spawner_brutal");
        public static final ResourceLocation SPAWNER_SWARM = Apotheosis.loc("chests/spawner_swarm");
        public static final ResourceLocation TOME_TOWER = Apotheosis.loc("chests/tome_tower");
    }



    public static final class DamageTypes {
        public static final ResourceKey<DamageType> EXECUTE = ResourceKey.create(Registries.DAMAGE_TYPE, Apotheosis.loc("execute"));
        public static final ResourceKey<DamageType> PSYCHIC = ResourceKey.create(Registries.DAMAGE_TYPE, Apotheosis.loc("psychic"));
        public static final ResourceKey<DamageType> CORRUPTED = ResourceKey.create(Registries.DAMAGE_TYPE, Apotheosis.loc("corrupted"));
    }

    public static Item registerItem(Item item, String path){
        return Registry.register(BuiltInRegistries.ITEM, Apotheosis.loc(path), item);
    }

    public static Enchantment registerEnchantment(String path, Enchantment enchantment){
        return Registry.register(BuiltInRegistries.ENCHANTMENT, Apotheosis.loc( path), enchantment);
    }

    public static void registerBlock(Block item, String id){
        Registry.register(BuiltInRegistries.BLOCK, Apotheosis.loc(id), item);
    }

    public static TagKey<Item> registerItemTag(ResourceLocation id) {
        return TagKey.create(Registries.ITEM, id);
    }

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> void registerSerializer(String id, S serializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Apotheosis.loc(id), serializer);
    }

    public static <M extends MenuType<T>, T extends AbstractContainerMenu> M registerMenu(String id, M menu) {
        return Registry.register(BuiltInRegistries.MENU, Apotheosis.loc(id), menu);
    }

    public static<T extends BlockEntity> BlockEntityType<T> registerBEType(String id, BlockEntityType<T> be) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Apotheosis.loc(id), be);
    }

    private static Potion registerPot(Potion potion, String name) {
        return Registry.register(BuiltInRegistries.POTION, Apotheosis.loc(name), potion);
    }
}
