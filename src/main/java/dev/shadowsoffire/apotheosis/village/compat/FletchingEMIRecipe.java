package dev.shadowsoffire.apotheosis.village.compat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FletchingEMIRecipe implements EmiRecipe {
    private final List<EmiIngredient> inputs;
    private final FletchingRecipe recipe;
    private final EmiStack output;
    public FletchingEMIRecipe(FletchingRecipe recipe) {
        this.recipe = recipe;
        this.inputs = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        this.output = EmiStack.of(recipe.getOutput());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VillageEMIPlugin.FLETCHING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return recipe.getId();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 140;
    }

    @Override
    public int getDisplayHeight() {
        return 55;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(FletchingScreen.TEXTURES, 1, 1, 139, 54, 6, 16);
        for (int i = 0; i < 3; i++) {
            widgets.addSlot(inputs.get(i), 42, 1 + i * 18);
        }
        widgets.addSlot(output, 114, 15).recipeContext(this).large(true);
    }
}
