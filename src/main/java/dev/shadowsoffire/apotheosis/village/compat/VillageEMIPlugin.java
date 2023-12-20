package dev.shadowsoffire.apotheosis.village.compat;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.compat.ZenithEMIPlugin;
import dev.shadowsoffire.apotheosis.village.VillageModule;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import net.minecraft.world.level.block.Blocks;

public class VillageEMIPlugin {
    public static EmiRecipeCategory FLETCHING = new EmiRecipeCategory(Apotheosis.loc("fletching"),
            EmiStack.of(Blocks.FLETCHING_TABLE), ZenithEMIPlugin.simplifiedRenderer(0, 0), EmiRecipeSorting.compareOutputThenInput());

    public static void register(EmiRegistry registry) {
        registry.addCategory(FLETCHING);
        registry.addWorkstation(FLETCHING, EmiStack.of(Blocks.FLETCHING_TABLE));

        for (FletchingRecipe recipe : registry.getRecipeManager().getAllRecipesFor(VillageModule.FLETCHING)) {
            ZenithEMIPlugin.addRecipeSafe(registry, () -> new FletchingEMIRecipe(recipe), recipe);
        }
    }
}
