package dev.shadowsoffire.apotheosis.adventure;

import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.*;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableBlock;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTile;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.*;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingBlock;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingMenu;
import dev.shadowsoffire.apotheosis.adventure.boss.BossSpawnerBlock;
import dev.shadowsoffire.apotheosis.adventure.boss.BossSummonerItem;
import dev.shadowsoffire.apotheosis.adventure.gen.BossDungeonFeature;
import dev.shadowsoffire.apotheosis.adventure.gen.BossDungeonFeature2;
import dev.shadowsoffire.apotheosis.adventure.gen.ItemFrameGemsProcessor;
import dev.shadowsoffire.apotheosis.adventure.gen.RogueSpawnerFeature;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.objects.GlowyBlockItem.GlowyItem;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class Adventure {

    //private static final DeferredHelper R = ModularDeferredHelper.create(() -> Apotheosis.enableAdventure);

    public static class Blocks {

        public static final BossSpawnerBlock BOSS_SPAWNER = new BossSpawnerBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());

        public static final ReforgingTableBlock SIMPLE_REFORGING_TABLE = new ReforgingTableBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2, 20F), 2);

        public static final ReforgingTableBlock REFORGING_TABLE = new ReforgingTableBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4, 1000F), 4);

        public static final SalvagingTableBlock SALVAGING_TABLE = new SalvagingTableBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.5F));

        public static final GemCuttingBlock GEM_CUTTING_TABLE = new GemCuttingBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.5F));

        private static void bootstrap() {}

    }

    public static class Items {

        public static final Item COMMON_MATERIAL = rarityMat("common");

        public static final Item UNCOMMON_MATERIAL = rarityMat("uncommon");

        public static final Item RARE_MATERIAL = rarityMat("rare");

        public static final Item EPIC_MATERIAL = rarityMat("epic");

        public static final Item MYTHIC_MATERIAL = rarityMat("mythic");

        public static final Item ANCIENT_MATERIAL = rarityMat("ancient");

        public static final Item GEM_DUST = new Item(new Item.Properties());

        public static final Item VIAL_OF_EXPULSION = new Item(new Item.Properties());

        public static final Item VIAL_OF_EXTRACTION = new Item(new Item.Properties());

        public static final Item VIAL_OF_UNNAMING = new Item(new Item.Properties());

        public static final Item SIGIL_OF_SOCKETING = new Item(new Item.Properties());

        public static final Item SUPERIOR_SIGIL_OF_SOCKETING = new GlowyItem(new Item.Properties());

        public static final Item SIGIL_OF_ENHANCEMENT = new Item(new Item.Properties());

        public static final Item SUPERIOR_SIGIL_OF_ENHANCEMENT =  new GlowyItem(new Item.Properties());

        public static final Item BOSS_SUMMONER = new BossSummonerItem(new Item.Properties());

        public static final Item SIMPLE_REFORGING_TABLE = new BlockItem(Blocks.SIMPLE_REFORGING_TABLE, new Item.Properties());

        public static final Item REFORGING_TABLE =new BlockItem(Blocks.REFORGING_TABLE, new Item.Properties());

        public static final Item SALVAGING_TABLE = new BlockItem(Blocks.SALVAGING_TABLE, new Item.Properties());

        public static final Item GEM_CUTTING_TABLE = new BlockItem(Blocks.GEM_CUTTING_TABLE, new Item.Properties());

        public static final Item GEM = new GemItem(new Item.Properties());

        private static Item rarityMat(String id) {
            return new SalvageItem(RarityRegistry.INSTANCE.holder(Apotheosis.loc(id)), new Item.Properties());
        }

        private static void bootstrap() {};

    }

    public static class Features {

        public static final ResourceLocation BOSS_DUNGEON_ID = Apotheosis.loc("boss_dungeon");

        public static final ResourceLocation BOSS_DUNGEON_2_ID = Apotheosis.loc("boss_dungeon_2");

        public static final ResourceLocation ROGUE_SPAWNER_ID = Apotheosis.loc("rogue_spawner");

        public static final BossDungeonFeature BOSS_DUNGEON = new BossDungeonFeature();

        public static final BossDungeonFeature2 BOSS_DUNGEON_2 = new BossDungeonFeature2();

        public static final RogueSpawnerFeature ROGUE_SPAWNER = new RogueSpawnerFeature();

    //    public static final StructureProcessorType<ItemFrameGemsProcessor> ITEM_FRAME_GEMS = R.custom("item_frame_gems", Registries.STRUCTURE_PROCESSOR, () -> () -> ItemFrameGemsProcessor.CODEC);

        private static void bootstrap() {
            AdventureModule.LOGGER.info("Registering Features!");
            Registry.register(BuiltInRegistries.FEATURE, BOSS_DUNGEON_ID, BOSS_DUNGEON);
            Registry.register(BuiltInRegistries.FEATURE, BOSS_DUNGEON_2_ID, BOSS_DUNGEON_2);
            Registry.register(BuiltInRegistries.FEATURE, ROGUE_SPAWNER_ID, ROGUE_SPAWNER);
        };

    }

    public static class Menus { //TODO rewrite ;)

    //    public static final MenuType<ReforgingMenu> REFORGING = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("reforging"), ReforgingMenu::new);

    //    public static final MenuType<SalvagingMenu> SALVAGE = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("salvage"), SalvagingMenu::new);

        public static final MenuType<GemCuttingMenu> GEM_CUTTING = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("gem_cutting"), GemCuttingMenu::new);
        public static final MenuType<SalvagingMenu> SALVAGE = Registry.register(BuiltInRegistries.MENU, Apotheosis.loc("salvage"), MenuUtil.posType(SalvagingMenu::new));
        public static final MenuType<GemCuttingMenu> REFORGING = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("reforging"), GemCuttingMenu::new);

        private static void bootstrap() {};
    }

    public static class RecipeTypes {
        public static final RecipeType<SalvagingRecipe> SALVAGING = PlaceboUtil.makeRecipeType("zenith:salvaging");
        public static final RecipeType<ReforgingRecipe> REFORGING = PlaceboUtil.makeRecipeType("zenith:reforging");
    }

    public static class Tabs {

        public static final ResourceKey<CreativeModeTab> ADVENTURE = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Apotheosis.loc("adventure"));
        public static final CreativeModeTab ADVENTURETAB = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.zenith.adventure"))
                .icon(Items.GEM::getDefaultInstance)
                .displayItems((a,b) -> {
                    Apoth.fill(b, Items.COMMON_MATERIAL, Items.UNCOMMON_MATERIAL, Items.RARE_MATERIAL, Items.EPIC_MATERIAL, Items.MYTHIC_MATERIAL, Items.GEM_DUST, Items.VIAL_OF_EXPULSION,
                            Items.VIAL_OF_EXTRACTION, Items.VIAL_OF_UNNAMING, Items.SIGIL_OF_SOCKETING, Items.SIGIL_OF_ENHANCEMENT, Items.SUPERIOR_SIGIL_OF_SOCKETING, Items.SUPERIOR_SIGIL_OF_ENHANCEMENT, Items.BOSS_SUMMONER,
                            Items.SIMPLE_REFORGING_TABLE, Items.REFORGING_TABLE, Items.SALVAGING_TABLE, Items.GEM_CUTTING_TABLE);

                    GemItem.fillItemCategory(b);
                })
                .build();
        private static void bootstrap() {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ADVENTURE, ADVENTURETAB);
        }
    }

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

    public static class Tiles{
        public static final BlockEntityType<BossSpawnerBlock.BossSpawnerTile> BOSS_SPAWNER = Apoth.registerBEType("boss_spawner", new BlockEntityType<>(BossSpawnerBlock.BossSpawnerTile::new, ImmutableSet.of(Ench.Blocks.ENDER_LIBRARY), null));
        public static final BlockEntityType<ReforgingTableTile> REFORGING_TABLE = Apoth.registerBEType("reforging_table", new TickingBlockEntityType<>(ReforgingTableTile::new, ImmutableSet.of(Adventure.Blocks.SIMPLE_REFORGING_TABLE, Adventure.Blocks.REFORGING_TABLE), true, false));
        public static final BlockEntityType<SalvagingTableTile> SALVAGING_TABLE = Apoth.registerBEType("salvaging_table", new BlockEntityType<>(SalvagingTableTile::new, ImmutableSet.of(Adventure.Blocks.SALVAGING_TABLE), null));
    }
    public static void bootstrap() {
        Blocks.bootstrap();
        Items.bootstrap();
        Features.bootstrap();
        Menus.bootstrap();
        Tabs.bootstrap();
    };

}
