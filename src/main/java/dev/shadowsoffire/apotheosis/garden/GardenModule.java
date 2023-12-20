package dev.shadowsoffire.apotheosis.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.placebo.config.Configuration;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;

import java.io.File;

public class GardenModule {

    public static int maxCactusHeight = 5;
    public static int maxReedHeight = 255;
    public static int maxBambooHeight = 32;

    public static final Item ENDER_LEAD = new EnderLeadItem();

    public static void init() {
        reload(false);
        items();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> {
            content.accept(ENDER_LEAD);
        });
    }

    public static void items() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Apotheosis.MODID, "ender_lead"), ENDER_LEAD);
        ComposterBlock.COMPOSTABLES.put(Blocks.CACTUS.asItem(), 0.5F);
        ComposterBlock.COMPOSTABLES.put(Blocks.SUGAR_CANE.asItem(), 0.5F);
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
