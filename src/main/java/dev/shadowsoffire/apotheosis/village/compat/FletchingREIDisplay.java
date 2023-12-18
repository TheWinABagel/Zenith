package dev.shadowsoffire.apotheosis.village.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public class FletchingREIDisplay implements Display {
    public static final CategoryIdentifier<FletchingREIDisplay> ID = CategoryIdentifier.of(Apotheosis.MODID, "fletching");

    private final FletchingRecipe recipe;

    public FletchingREIDisplay(FletchingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).toList();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(recipe.getOutput()));
    }

    public FletchingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }

}
