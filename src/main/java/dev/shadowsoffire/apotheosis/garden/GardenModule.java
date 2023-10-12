package dev.shadowsoffire.apotheosis.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.recipe.RecipeHelper;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;

import java.io.File;

public class GardenModule {

    public static int maxCactusHeight = 5;
    public static int maxReedHeight = 255;
    public static int maxBambooHeight = 32;

    public static final Item ENDER_LEAD = new EnderLeadItem();

    private static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ENDER_LEAD))
            .title(Component.translatable("zenith.creative_tab"))
            .build();

    public static void init() {
        reload(false);
    //    Apotheosis.HELPER.registerProvider(factory -> {
    //        factory.addShapeless(ENDER_LEAD, Items.ENDER_PEARL, Items.LEAD, Items.GOLD_INGOT);
    //    });
        items();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> {
        content.accept(ENDER_LEAD);
        });
    }



    public static void items() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Apotheosis.MODID, "ender_lead"), ENDER_LEAD);
        ComposterBlock.COMPOSTABLES.put(Blocks.CACTUS.asItem(), 0.5F);
        ComposterBlock.COMPOSTABLES.put(Blocks.SUGAR_CANE.asItem(), 0.5F);

        //TabFillingRegistry.register(CreativeModeTabs.TOOLS_AND_UTILITIES, ENDER_LEAD);
    }

    public static void reload(boolean e) {
        Configuration c = new Configuration(new File(Apotheosis.configDir, "garden.cfg"));
        c.setTitle("Zenith Garden Module Configuration");
        maxCactusHeight = c.getInt("Cactus Height", "general", maxCactusHeight, 1, 512, "The max height a stack of cacti may grow to.  Vanilla is 3.  Values greater than 32 are uncapped growth.\nServer-authoritative.");
        maxReedHeight = c.getInt("Reed Height", "general", maxReedHeight, 1, 512, "The max height a stack of reeds may grow to.  Vanilla is 3.  Values greater than 32 are uncapped growth.\nServer-authoritative.");
        maxBambooHeight = c.getInt("Bamboo Height", "general", maxBambooHeight, 1, 64, "The max height a stack of bamboo may grow to.  Vanilla is 16.\nServer-authoritative.");
        if (!e && c.hasChanged()) c.save();
    }

}
