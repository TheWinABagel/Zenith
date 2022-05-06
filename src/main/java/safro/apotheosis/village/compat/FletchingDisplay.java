package safro.apotheosis.village.compat;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.village.fletching.FletchingRecipe;

import java.util.Collections;
import java.util.List;

public class FletchingDisplay implements Display {
    public static final CategoryIdentifier<FletchingDisplay> ID = CategoryIdentifier.of(new ResourceLocation(Apotheosis.MODID, "fletching"));
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;
    private final FletchingRecipe recipe;

    public FletchingDisplay(FletchingRecipe recipe) {
        this.input = EntryIngredients.ofIngredients(recipe.getIngredients());
        this.output = Collections.singletonList(EntryIngredients.ofItemStacks(ImmutableList.of(recipe.getResultItem())));
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

    public FletchingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
