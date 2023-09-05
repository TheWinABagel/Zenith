package dev.shadowsoffire.apotheosis.ench.compat;

import com.google.common.collect.ImmutableList;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;
import java.util.List;

public class EnchantingREIDisplay implements Display {
    public static final CategoryIdentifier<EnchantingREIDisplay> ID = CategoryIdentifier.of(Apotheosis.MODID, "enchanting");

    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;
    private final EnchantingRecipe recipe;

    public EnchantingREIDisplay(EnchantingRecipe recipe) {
        this.input = Collections.singletonList(EntryIngredients.ofIngredient(recipe.getInput()));
        this.output = Collections.singletonList(EntryIngredients.ofItemStacks(ImmutableList.of(recipe.getOutput())));
        this.recipe = recipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    public EnchantingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
