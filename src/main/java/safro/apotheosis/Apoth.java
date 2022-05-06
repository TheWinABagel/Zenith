package safro.apotheosis;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Apoth {
    public static final TagKey<Item> BOON_DROPS = registerItem(new ResourceLocation(Apotheosis.MODID, "boon_drops"));
    public static final TagKey<Item> SPEARFISHING_DROPS = registerItem(new ResourceLocation(Apotheosis.MODID, "spearfishing_drops"));
    public static final TagKey<Item> ENCHANT_FUELS = registerItem(new ResourceLocation(Apotheosis.MODID, "enchant_fuels"));

    public static final TagKey<Item> IRON_BLOCKS = registerItem(new ResourceLocation("c", "iron_blocks"));

    public static void init() {}

    public static TagKey<Item> registerItem(ResourceLocation id) {
        return TagKey.create(Registry.ITEM_REGISTRY, id);
    }

    public static TagKey<Block> registerBlock(ResourceLocation id) {
        return TagKey.create(Registry.BLOCK_REGISTRY, id);
    }
}
