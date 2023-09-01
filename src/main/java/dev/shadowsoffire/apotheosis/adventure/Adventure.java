package dev.shadowsoffire.apotheosis.adventure;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableBlock;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingTableBlock;
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
import dev.shadowsoffire.apotheosis.ench.objects.GlowyBlockItem.GlowyItem;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
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

    //    public static final RegistryObject<BossDungeonFeature> BOSS_DUNGEON = R.feature("boss_dungeon", BossDungeonFeature::new);

    //    public static final RegistryObject<BossDungeonFeature2> BOSS_DUNGEON_2 = R.feature("boss_dungeon_2", BossDungeonFeature2::new);

    //    public static final RegistryObject<RogueSpawnerFeature> ROGUE_SPAWNER = R.feature("rogue_spawner", RogueSpawnerFeature::new);

    //    public static final RegistryObject<StructureProcessorType<ItemFrameGemsProcessor>> ITEM_FRAME_GEMS = R.custom("item_frame_gems", Registries.STRUCTURE_PROCESSOR, () -> () -> ItemFrameGemsProcessor.CODEC);

        private static void bootstrap() {};

    }

    public static class Menus {

    //    public static final MenuType<ReforgingMenu> REFORGING = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("reforging"), ReforgingMenu::new);

    //    public static final MenuType<SalvagingMenu> SALVAGE = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("salvage"), SalvagingMenu::new);

        public static final MenuType<GemCuttingMenu> GEM_CUTTING = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("gem_cutting"), GemCuttingMenu::new);
        public static final MenuType<GemCuttingMenu> SALVAGE = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("salvage"), GemCuttingMenu::new);
        public static final MenuType<GemCuttingMenu> REFORGING = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("reforging"), GemCuttingMenu::new);

        private static void bootstrap() {};
    }

    public static class Tabs {
/*
        public static final RegistryObject<CreativeModeTab> ADVENTURE = R.tab("adventure",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.apotheosis.adventure")).icon(Items.GEM::getDefaultInstance).build());
*/
        private static void bootstrap() {}
    }

    public static void bootstrap() {
        Blocks.bootstrap();
        Items.bootstrap();
        Features.bootstrap();
        Menus.bootstrap();
        Tabs.bootstrap();
    };

}
