package dev.shadowsoffire.apotheosis;


import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Object Holder Class. For the main mod class, see {@link Apotheosis}
 */
public class Apoth {


    public static final class Menus {
        public static void bootstrap(){}
    }

    public static final class Tiles {
        public static void bootstrap(){}
        public static final BlockEntityType<AnvilTile> ANVIL_TILE = Apoth.registerBEType("anvil", new BlockEntityType<>(AnvilTile::new, ImmutableSet.of(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL), null));
    }

    public static final class Tags {
        public static final TagKey<Item> ENCHANT_LEVEL_MODIFIER_BLACKLIST = registerItemTag(Apotheosis.loc("enchant_level_modifier_blacklist"));
        public static final TagKey<Item> BOON_DROPS = registerItemTag(Apotheosis.loc("boon_drops"));
        public static final TagKey<Item> SPEARFISHING_DROPS = registerItemTag(Apotheosis.loc("spearfishing_drops"));
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

    public static void registerBlock(Block block, String id){
        Registry.register(BuiltInRegistries.BLOCK, Apotheosis.loc(id), block);
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

    public static Potion registerPot(Potion potion, String name) {
        return Registry.register(BuiltInRegistries.POTION, Apotheosis.loc(name), potion);
    }

    public static void fill(CreativeModeTab.Output output, Item... items) {
        Arrays.stream(items).forEach(output::accept);
    }

    public static void fill(CreativeModeTab.Output output, Enchantment... enchants) {
        Arrays.stream(enchants).map(Apoth::enchFiller).forEach(books -> books.stream().toList().forEach(output::accept));
    }

    public static ArrayList<ItemStack> enchFiller(Enchantment ench) {
        ArrayList<ItemStack> out = new ArrayList<>();
        int maxLevel = EnchHooks.getMaxLevel(ench);
        out.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, maxLevel)));
        return out;
    }

    public static <T> T callWhenOn(EnvType env, Supplier<Callable<T>> toRun) {
        return unsafeCallWhenOn(env, toRun);
    }

    public static <T> T unsafeCallWhenOn(EnvType env, Supplier<Callable<T>> toRun) {
        if (FabricLoader.getInstance().getEnvironmentType() == env) {
            try {
                return toRun.get().call();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
