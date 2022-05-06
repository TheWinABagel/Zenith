package safro.apotheosis.spawn.compat;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.spawn.modifiers.SpawnerModifier;
import safro.apotheosis.village.fletching.FletchingRecipe;

import java.util.Collections;
import java.util.List;

public class SpawnerDisplay implements Display {
    public static final CategoryIdentifier<SpawnerDisplay> ID = CategoryIdentifier.of(new ResourceLocation(Apotheosis.MODID, "spawner_modifier"));
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;
    private final SpawnerModifier recipe;

    public SpawnerDisplay(SpawnerModifier recipe) {
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

    public SpawnerModifier getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
