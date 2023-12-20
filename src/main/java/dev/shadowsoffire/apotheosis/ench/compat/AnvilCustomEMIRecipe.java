package dev.shadowsoffire.apotheosis.ench.compat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AnvilCustomEMIRecipe implements EmiRecipe {
    private final EmiStack firstStack;
    private final EmiStack secondStack;
    private final EmiStack output;
    private final ResourceLocation id;

    public AnvilCustomEMIRecipe(EmiStack firstStack, EmiStack secondStack, EmiStack output, ResourceLocation id) {
        this.firstStack = firstStack;
        this.secondStack = secondStack;
        this.output = output;
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(firstStack, secondStack);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addSlot(firstStack, 0, 0);
        widgets.addSlot(secondStack, 49, 0);
        widgets.addSlot(output, 107, 0).recipeContext(this);
    }
}
