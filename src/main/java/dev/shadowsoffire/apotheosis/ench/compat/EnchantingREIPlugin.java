package dev.shadowsoffire.apotheosis.ench.compat;

import com.google.common.collect.ImmutableMap;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.util.REIUtil;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.anvil.AnvilRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class EnchantingREIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Apotheosis.loc("infusion_enchanting").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableEnch) return;
        registry.add(new EnchantingREICategory());
        registry.addWorkstations(EnchantingREIDisplay.ID, EntryIngredients.ofItemStacks(stackOf(Blocks.ENCHANTING_TABLE)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableEnch) return;
        ItemStack enchDiaSword = new ItemStack(Items.DIAMOND_SWORD);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SHARPNESS, 1), enchDiaSword);
        ItemStack cursedDiaSword = new ItemStack(Items.DIAMOND_SWORD);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.BINDING_CURSE, 1), cursedDiaSword);
        ItemStack enchBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SHARPNESS, 1), enchBook);

        registry.add(new AnvilRecipe(null, List.of(enchDiaSword), stackOf(Blocks.COBWEB), stackOf(Items.DIAMOND_SWORD)));
        registry.add(new AnvilRecipe(null, List.of(cursedDiaSword), stackOf(Ench.Items.PRISMATIC_WEB), stackOf(Items.DIAMOND_SWORD)));
        registry.add(new AnvilRecipe(null, List.of(enchDiaSword), stackOf(Ench.Items.SCRAP_TOME), List.of(enchBook)));
        registry.add(new AnvilRecipe(null, stackOf(Items.DAMAGED_ANVIL), stackOf(Blocks.IRON_BLOCK), stackOf(Blocks.ANVIL)));

        registry.registerFiller(EnchantingRecipe.class, EnchantingREIDisplay::new);

        REIUtil.addInfo(registry, Ench.Items.LIBRARY, "info.zenith.library");
        REIUtil.addInfo(registry, Blocks.ENCHANTING_TABLE, "info.zenith.enchanting");
    }

    public static List<ItemStack> stackOf(ItemLike item) {
        return List.of(new ItemStack(item));
    }
}
