package dev.shadowsoffire.apotheosis;


import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryContainer;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryTile;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.garden.EnderLeadItem;

//import dev.shadowsoffire.apotheosis.village.fletching.arrows.*;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.spawner.ApothSpawnerBlock;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingContainer;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.*;
import dev.shadowsoffire.placebo.registry.RegObjHelper;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Object Holder Class. For the main mod class, see {@link Apotheosis}
 */
public class Apoth {

    public static final RegObjHelper R = new RegObjHelper(Apotheosis.MODID);

    public static final class Items {
    //    public static final RegistryObject<PotionCharmItem> POTION_CHARM = R.item("POTION_CHARM");
        public static final RegistryObject<Item> LUCKY_FOOT = R.item("LUCKY_FOOT");
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
        public static final RegistryObject<Potion> RESISTANCE = R.potion("RESISTANCE");
        public static final RegistryObject<Potion> LONG_RESISTANCE = R.potion("LONG_RESISTANCE");
        public static final RegistryObject<Potion> STRONG_RESISTANCE = R.potion("STRONG_RESISTANCE");
        public static final RegistryObject<Potion> ABSORPTION = R.potion("ABSORPTION");
        public static final RegistryObject<Potion> LONG_ABSORPTION = R.potion("LONG_ABSORPTION");
        public static final RegistryObject<Potion> STRONG_ABSORPTION = R.potion("STRONG_ABSORPTION");
        public static final RegistryObject<Potion> HASTE = R.potion("HASTE");
        public static final RegistryObject<Potion> LONG_HASTE = R.potion("LONG_HASTE");
        public static final RegistryObject<Potion> STRONG_HASTE = R.potion("STRONG_HASTE");
        public static final RegistryObject<Potion> FATIGUE = R.potion("FATIGUE");
        public static final RegistryObject<Potion> LONG_FATIGUE = R.potion("LONG_FATIGUE");
        public static final RegistryObject<Potion> STRONG_FATIGUE = R.potion("STRONG_FATIGUE");
        public static final RegistryObject<Potion> SUNDERING = R.potion("SUNDERING");
        public static final RegistryObject<Potion> LONG_SUNDERING = R.potion("LONG_SUNDERING");
        public static final RegistryObject<Potion> STRONG_SUNDERING = R.potion("STRONG_SUNDERING");
        public static final RegistryObject<Potion> KNOWLEDGE = R.potion("KNOWLEDGE");
        public static final RegistryObject<Potion> LONG_KNOWLEDGE = R.potion("LONG_KNOWLEDGE");
        public static final RegistryObject<Potion> STRONG_KNOWLEDGE = R.potion("STRONG_KNOWLEDGE");
        public static final RegistryObject<Potion> WITHER = R.potion("WITHER");
        public static final RegistryObject<Potion> LONG_WITHER = R.potion("LONG_WITHER");
        public static final RegistryObject<Potion> STRONG_WITHER = R.potion("STRONG_WITHER");
        public static final RegistryObject<Potion> VITALITY = R.potion("VITALITY");
        public static final RegistryObject<Potion> LONG_VITALITY = R.potion("LONG_VITALITY");
        public static final RegistryObject<Potion> STRONG_VITALITY = R.potion("STRONG_VITALITY");
        public static final RegistryObject<Potion> GRIEVOUS = R.potion("GRIEVOUS");
        public static final RegistryObject<Potion> LONG_GRIEVOUS = R.potion("LONG_GRIEVOUS");
        public static final RegistryObject<Potion> STRONG_GRIEVOUS = R.potion("STRONG_GRIEVOUS");
    }
    public static final class Blocks {
        public static final Block SPAWNER_TEST = new ApothSpawnerBlock();
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
        //public static final RegistryObject<BlockEntityType<BlockEntity>> LIBRARY = R.blockEntity("LIBRARY");

        //public static final RegistryObject<BlockEntityType<BlockEntity>> ENDER_LIBRARY = R.blockEntity("ENDER_LIBRARY");
    //      public static final RegistryObject<BlockEntityType<BossSpawnerTile>> BOSS_SPAWNER = R.blockEntity("BOSS_SPAWNER");
    //    public static final RegistryObject<BlockEntityType<ReforgingTableTile>> REFORGING_TABLE = R.blockEntity("REFORGING_TABLE");
    //    public static final RegistryObject<BlockEntityType<SalvagingTableTile>> SALVAGING_TABLE = R.blockEntity("SALVAGING_TABLE");
    }
    /*
        public static final class Affixes {
            // Implicit affixes
            public static final DynamicHolder<SocketAffix> SOCKET = AffixRegistry.INSTANCE.holder(Apotheosis.loc("socket"));
            public static final DynamicHolder<DurableAffix> DURABLE = AffixRegistry.INSTANCE.holder(Apotheosis.loc("durable"));
            // Real affixes
            public static final DynamicHolder<MagicalArrowAffix> MAGICAL = AffixRegistry.INSTANCE.holder(Apotheosis.loc("ranged/special/magical"));
            public static final DynamicHolder<FestiveAffix> FESTIVE = AffixRegistry.INSTANCE.holder(Apotheosis.loc("sword/special/festive"));
            public static final DynamicHolder<TelepathicAffix> TELEPATHIC = AffixRegistry.INSTANCE.holder(Apotheosis.loc("telepathic"));
            public static final DynamicHolder<OmneticAffix> OMNETIC = AffixRegistry.INSTANCE.holder(Apotheosis.loc("breaker/special/omnetic"));
            public static final DynamicHolder<RadialAffix> RADIAL = AffixRegistry.INSTANCE.holder(Apotheosis.loc("breaker/special/radial"));
        }
*/
        public static final class Tags {
            public static final TagKey<Item> BOON_DROPS = registerItemTag(new ResourceLocation(Apotheosis.MODID, "boon_drops"));
            public static final TagKey<Item> SPEARFISHING_DROPS = registerItemTag(new ResourceLocation(Apotheosis.MODID, "spearfishing_drops"));
        }

    public static final class RecipeTypes {
        public static final RecipeType<FletchingRecipe> FLETCHING = PlaceboUtil.makeRecipeType("apotheosis:fletching");
        public static final RecipeType<EnchantingRecipe> INFUSION = PlaceboUtil.makeRecipeType("apotheosis:enchanting");
        public static final RecipeType<SpawnerModifier> MODIFIER = PlaceboUtil.makeRecipeType("apotheosis:spawner_modifier");
    //    public static final RecipeType<SalvagingRecipe> SALVAGING = PlaceboUtil.makeRecipeType("apotheosis:salvaging");
    //    public static final RecipeType<ReforgingRecipe> REFORGING = PlaceboUtil.makeRecipeType("apotheosis:reforging");
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

    public static Item registerItem(String path, Item item){
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Apotheosis.MODID, path), item);
    }

    public static Enchantment registerEnchantment(String path, Enchantment enchantment){
        return Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(Apotheosis.MODID, path), enchantment);
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

}
