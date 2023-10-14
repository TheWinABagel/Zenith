package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SalvagingREIDisplay implements Display {
    public static final CategoryIdentifier<SalvagingREIDisplay> ID = CategoryIdentifier.of(Apotheosis.MODID, "salvaging");

    private final SalvagingRecipe recipe;
    private final List<EntryIngredient> input;

    public SalvagingREIDisplay(SalvagingRecipe recipe) {
        this.recipe = recipe;
        this.input = Collections.singletonList(EntryIngredients.ofIngredient(recipe.getInput()));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> outputs = new ArrayList<>();
        for (var d : recipe.getOutputs()) {
            outputs.add(EntryIngredients.of(d.getStack()));
        }
        return outputs;
    }

    public SalvagingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }

}
