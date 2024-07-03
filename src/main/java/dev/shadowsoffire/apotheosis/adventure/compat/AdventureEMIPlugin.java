package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.compat.ZenithEMIPlugin;
import dev.shadowsoffire.apotheosis.util.GemIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AdventureEMIPlugin {

    public static EmiRecipeCategory SALVAGING = new EmiRecipeCategory(Apotheosis.loc("salvaging"),
            EmiStack.of(Adventure.Blocks.SALVAGING_TABLE), ZenithEMIPlugin.simplifiedRenderer(0, 0), EmiRecipeSorting.compareInputThenOutput());
    public static EmiRecipeCategory GEM_CUTTING = new EmiRecipeCategory(Apotheosis.loc("gem_cutting"),
            EmiStack.of(Adventure.Blocks.GEM_CUTTING_TABLE), ZenithEMIPlugin.simplifiedRenderer(0, 0), EmiRecipeSorting.compareInputThenOutput());

    public static void register(EmiRegistry registry) {
        registry.addCategory(SALVAGING);
        registry.addCategory(GEM_CUTTING);
        registry.addWorkstation(SALVAGING, EmiStack.of(Adventure.Blocks.SALVAGING_TABLE));
        registry.addWorkstation(GEM_CUTTING, EmiStack.of(Adventure.Blocks.GEM_CUTTING_TABLE));

        List<Ingredient> gemlist = new ArrayList<>();
        RarityRegistry.INSTANCE.getValues().forEach(lootRarity -> gemlist.add(new GemIngredient(RarityRegistry.INSTANCE.holder(lootRarity)).toVanilla()));

        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(gemlist.stream().map(EmiIngredient::of).toList(), List.of(Component.translatable("info.zenith.socketing")), Apotheosis.loc("socketing_info")));

        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(List.of(EmiStack.of(Adventure.Items.GEM_DUST)), List.of(Component.translatable("info.zenith.gem_crushing")), Apotheosis.loc("gem_crushing_info")));

        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(List.of(EmiStack.of(Adventure.Items.SIGIL_OF_SOCKETING)), List.of(Component.translatable("info.zenith.unnaming")), Apotheosis.loc("unnaming_info")));


        for (SalvagingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(Adventure.RecipeTypes.SALVAGING)) {
            ZenithEMIPlugin.addRecipeSafe(registry, () -> new SalvagingEMIRecipe(recipe), recipe);
        }

        for (Gem gem : GemRegistry.INSTANCE.getValues()) {
            LootRarity rarity = RarityRegistry.getMinRarity().get();
            LootRarity max = RarityRegistry.getMaxRarity().get();
            while (rarity != max) {
                if (gem.clamp(rarity) == rarity) {
                    LootRarity lambdaDumb = rarity;
                    ZenithEMIPlugin.addRecipeSafe(registry, () -> new GemCuttingEMIRecipe(gem, lambdaDumb));
                }
                rarity = rarity.next();
            }
        }
    }
}
