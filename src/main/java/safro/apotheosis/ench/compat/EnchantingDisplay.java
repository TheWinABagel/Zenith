package safro.apotheosis.ench.compat;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.ench.table.EnchantingRecipe;

import java.util.Collections;
import java.util.List;

public class EnchantingDisplay implements Display {
    public static final CategoryIdentifier<EnchantingDisplay> ID = CategoryIdentifier.of(new ResourceLocation(Apotheosis.MODID, "enchanting"));
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;
    private final EnchantingRecipe recipe;

    public EnchantingDisplay(EnchantingRecipe recipe) {
        this.input = Collections.singletonList(EntryIngredients.ofIngredient(recipe.getInput()));
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

    public EnchantingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
