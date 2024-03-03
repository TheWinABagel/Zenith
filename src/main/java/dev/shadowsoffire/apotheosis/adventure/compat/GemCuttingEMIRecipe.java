package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingMenu;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GemCuttingEMIRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURES = new ResourceLocation(Apotheosis.MODID, "textures/gui/gem_cutting_jei.png");

    protected final ItemStack out, gem, dust;
    protected final ItemStack[] materials;
    private final ResourceLocation id;

    public GemCuttingEMIRecipe(Gem gem, LootRarity rarity) {
        this.out = GemRegistry.createGemStack(gem, rarity.next());
        this.gem = GemRegistry.createGemStack(gem, rarity);
        this.dust = new ItemStack(Adventure.Items.GEM_DUST, GemCuttingMenu.getDustCost(rarity));
        LootRarity min = RarityRegistry.getMinRarity().get();
        if (rarity == min) {
            this.materials = new ItemStack[2];
            this.materials[0] = new ItemStack(rarity.getMaterial(), GemCuttingMenu.STD_MAT_COST);
            this.materials[1] = new ItemStack(rarity.next().getMaterial(), GemCuttingMenu.NEXT_MAT_COST);
        }
        else if ("ancient".equals(RarityRegistry.INSTANCE.getKey(rarity.next()).getPath())) { // Special case ancient because the material is unavailable.
            this.materials = new ItemStack[2];
            this.materials[0] = new ItemStack(rarity.prev().getMaterial(), GemCuttingMenu.PREV_MAT_COST);
            this.materials[1] = new ItemStack(rarity.getMaterial(), GemCuttingMenu.STD_MAT_COST);
        }
        else {
            this.materials = new ItemStack[3];
            this.materials[0] = new ItemStack(rarity.prev().getMaterial(), GemCuttingMenu.PREV_MAT_COST);
            this.materials[1] = new ItemStack(rarity.getMaterial(), GemCuttingMenu.STD_MAT_COST);
            this.materials[2] = new ItemStack(rarity.next().getMaterial(), GemCuttingMenu.NEXT_MAT_COST);
        }
        this.id = Apotheosis.loc(gem.getId().getPath() + "." + rarity.getMaterial().getDescriptionId());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return AdventureEMIPlugin.GEM_CUTTING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.of(gem), EmiStack.of(dust), EmiStack.of(gem));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return Arrays.stream(materials).map(EmiStack::of).toList();
    }

    @Override
    public int getDisplayWidth() {
        return 148;
    }

    @Override
    public int getDisplayHeight() {
        return 78;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURES, 1, 1, 148, 78, 0, 0);
        widgets.addSlot(EmiStack.of(gem), 46, 14).drawBack(false);
        widgets.addSlot(EmiStack.of(dust), 5, 14).drawBack(false);
        widgets.addSlot(EmiStack.of(gem), 46, 57).drawBack(false);
        widgets.addSlot(EmiIngredient.of(Ingredient.of(materials)), 87, 14).drawBack(false);
        widgets.addSlot(EmiStack.of(out), 129, 14).recipeContext(this).drawBack(false);
    }
}
