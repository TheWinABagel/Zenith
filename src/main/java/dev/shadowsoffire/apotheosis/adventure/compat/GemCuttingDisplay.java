package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingMenu;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GemCuttingDisplay implements Display {
    public static final CategoryIdentifier<GemCuttingDisplay> ID = CategoryIdentifier.of(Apotheosis.MODID, "gem_cutting");

    private final GemCuttingRecipe recipe;

    public GemCuttingDisplay(GemCuttingRecipe recipe){
        this.recipe = recipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<ItemStack> list = new ArrayList<>();
            list.add(recipe.gem);
            list.add(recipe.dust);
            list.add(recipe.gem);
            list.addAll(Arrays.asList(recipe.materials));

        return list.stream().map(EntryIngredients::of).toList();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(recipe.out));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }

    public GemCuttingRecipe getRecipe() {
        return recipe;
    }

    public static class GemCuttingRecipe {

        protected final ItemStack out, gem, dust;
        protected final ItemStack[] materials;

        public GemCuttingRecipe(Gem gem, LootRarity rarity) {
            this.out = GemRegistry.createGemStack(gem, rarity.next());
            this.gem = GemRegistry.createGemStack(gem, rarity);
            this.dust = new ItemStack(Adventure.Items.GEM_DUST, GemCuttingMenu.getDustCost(rarity));
            LootRarity min = RarityRegistry.getMinRarity().get();
            if (rarity == min) {
                this.materials = new ItemStack[2];
                this.materials[0] = new ItemStack(rarity.getMaterial(), GemCuttingMenu.STD_MAT_COST);
                this.materials[1] = new ItemStack(rarity.next().getMaterial(), GemCuttingMenu.NEXT_MAT_COST);
            }
            else if (RarityRegistry.INSTANCE.getKey(rarity.next()).getPath().equals("ancient")) { // Special case ancient because the material is unavailable.
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
        }
    }
}
