package dev.shadowsoffire.apotheosis.ench.compat;

import com.google.common.collect.ImmutableMap;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.compat.ZenithEMIPlugin;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class EnchEMIPlugin {
    public static EmiRecipeCategory ENCHANTING = new EmiRecipeCategory(Apotheosis.loc("enchanting"),
            EmiStack.of(Blocks.ENCHANTING_TABLE), ZenithEMIPlugin.simplifiedRenderer(0, 0),
            (r1, r2) -> Float.compare(((EnchantingEMIRecipe) r1).getEterna(), ((EnchantingEMIRecipe) r2).getEterna()));

    public static void register(EmiRegistry registry) {
        registry.addCategory(ENCHANTING);
        registry.addWorkstation(ENCHANTING, EmiStack.of(Blocks.ENCHANTING_TABLE));

        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(List.of(EmiStack.of(Blocks.ENCHANTING_TABLE)), List.of(Component.translatable("info.zenith.enchanting")), Apotheosis.syntheticLoc("enchanting_info")));

        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(List.of(EmiStack.of(Ench.Blocks.LIBRARY)), List.of(Component.translatable("info.zenith.library")), Apotheosis.syntheticLoc("library_info")));

        for (EnchantingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(Ench.RecipeTypes.INFUSION)) {
            ZenithEMIPlugin.addRecipeSafe(registry, () -> new EnchantingEMIRecipe(recipe), recipe);
        }

        ItemStack enchDiaSword = new ItemStack(Items.DIAMOND_SWORD);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SHARPNESS, 1), enchDiaSword);
        ItemStack cursedDiaSword = new ItemStack(Items.DIAMOND_SWORD);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.BINDING_CURSE, 1), cursedDiaSword);
        ItemStack enchBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SHARPNESS, 1), enchBook);

        ZenithEMIPlugin.addRecipeSafe(registry, () -> new AnvilCustomEMIRecipe(EmiStack.of(enchDiaSword), EmiStack.of(Blocks.COBWEB),
               EmiStack.of(Items.DIAMOND_SWORD), Apotheosis.syntheticLoc("zenith_custom_cobweb_anvil")));
        ZenithEMIPlugin.addRecipeSafe(registry, () -> new AnvilCustomEMIRecipe(EmiStack.of(cursedDiaSword), EmiStack.of(Ench.Items.PRISMATIC_WEB),
                EmiStack.of(Items.DIAMOND_SWORD), Apotheosis.syntheticLoc("zenith_custom_prismatic_cobweb_anvil")));
        ZenithEMIPlugin.addRecipeSafe(registry, () -> new AnvilCustomEMIRecipe(EmiStack.of(enchDiaSword), EmiStack.of(Ench.Items.SCRAP_TOME),
                EmiStack.of(enchBook), Apotheosis.syntheticLoc("zenith_scrap_tome_anvil")));
        ZenithEMIPlugin.addRecipeSafe(registry, () -> new AnvilCustomEMIRecipe(EmiStack.of(Items.DAMAGED_ANVIL), EmiStack.of(Blocks.IRON_BLOCK),
                EmiStack.of(Blocks.ANVIL), Apotheosis.syntheticLoc("zenith_anvil_repair")));
    }
}
