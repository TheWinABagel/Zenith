package safro.zenith.village.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import safro.zenith.Zenith;
import safro.zenith.village.fletching.FletchingRecipe;

import java.util.List;

public class VillageREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Zenith.MODID, "village").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Zenith.enableVillage) return;
        registry.add(new FletchingCategory());
        registry.addWorkstations(FletchingDisplay.ID, EntryIngredients.ofItemStacks(stack(Blocks.FLETCHING_TABLE)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Zenith.enableVillage) return;
        registry.registerFiller(FletchingRecipe.class, FletchingDisplay::new);
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        if (!Zenith.enableVillage) return;
        registry.register(new FletchingTransferHandler());
    }

    public static List<ItemStack> stack(ItemLike item) {
        return List.of(new ItemStack(item));
    }
}
