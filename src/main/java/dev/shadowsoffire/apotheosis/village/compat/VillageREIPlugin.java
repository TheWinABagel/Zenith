package dev.shadowsoffire.apotheosis.village.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class VillageREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return Apotheosis.loc("village").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableVillage) return;
        registry.add(new FletchingREICategory());
        registry.addWorkstations(FletchingREIDisplay.ID, EntryIngredients.ofItemStacks(List.of(new ItemStack(Blocks.FLETCHING_TABLE))));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableVillage) return;
        registry.registerFiller(FletchingRecipe.class, FletchingREIDisplay::new);
    }
}
