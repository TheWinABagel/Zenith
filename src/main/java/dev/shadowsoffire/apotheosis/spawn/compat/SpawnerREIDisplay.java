package dev.shadowsoffire.apotheosis.spawn.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpawnerREIDisplay implements Display {
    public static final CategoryIdentifier<SpawnerREIDisplay> ID = CategoryIdentifier.of(Apotheosis.MODID, "spawner");

    private final SpawnerModifier recipe;

    public SpawnerREIDisplay(SpawnerModifier recipe) {
        this.recipe = recipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        var mutable = new ArrayList<EntryIngredient>();
        mutable.add(EntryIngredients.ofIngredient(recipe.getMainhandInput()));
        mutable.add(EntryIngredients.of(new ItemStack(Blocks.SPAWNER)));
        if (recipe.getOffhandInput() != Ingredient.EMPTY) mutable.add(EntryIngredients.ofIngredient(recipe.getOffhandInput()));
        return mutable;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(EntryIngredients.of(new ItemStack(Blocks.SPAWNER)));
    }

    public SpawnerModifier getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }

}
