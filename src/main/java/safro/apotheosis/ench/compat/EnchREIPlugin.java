package safro.apotheosis.ench.compat;

import com.google.common.collect.ImmutableMap;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.anvil.AnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.ench.EnchModule;
import safro.apotheosis.ench.table.EnchantingRecipe;
import safro.apotheosis.util.ClientUtil;

import java.util.List;

public class EnchREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Apotheosis.MODID, "enchantment").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableEnch) return;
        registry.add(new EnchantingCategory());
        registry.addWorkstations(EnchantingDisplay.ID, EntryIngredients.ofItemStacks(stack(Blocks.ENCHANTING_TABLE)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableEnch) return;
        ItemStack enchDiaSword = new ItemStack(Items.DIAMOND_SWORD);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SHARPNESS, 1), enchDiaSword);
        ItemStack cursedDiaSword = new ItemStack(Items.DIAMOND_SWORD);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.BINDING_CURSE, 1), cursedDiaSword);
        ItemStack enchBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SHARPNESS, 1), enchBook);

        registry.add(new AnvilRecipe(null, List.of(enchDiaSword), stack(Blocks.COBWEB), stack(Items.DIAMOND_SWORD)));
        registry.add(new AnvilRecipe(null, List.of(cursedDiaSword), stack(EnchModule.PRISMATIC_WEB), stack(Items.DIAMOND_SWORD)));
        registry.add(new AnvilRecipe(null, List.of(enchDiaSword), stack(EnchModule.SCRAP_TOME), List.of(enchBook)));
        registry.add(new AnvilRecipe(null, stack(Items.DAMAGED_ANVIL), stack(Blocks.IRON_BLOCK), stack(Blocks.ANVIL)));

        registry.registerFiller(EnchantingRecipe.class, EnchantingDisplay::new);
        List<EnchantingRecipe> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(EnchModule.INFUSION_RECIPE);
        recipes.sort((r1, r2) -> Float.compare(r1.getRequirements().eterna, r2.getRequirements().eterna));
        recipes.forEach(registry::add);

        ClientUtil.addInfo(registry, EnchModule.LIBRARY, "info.apotheosis.library");
        ClientUtil.addInfo(registry, Blocks.ENCHANTING_TABLE, "info.apotheosis.enchanting");
    }

    public static List<ItemStack> stack(ItemLike item) {
        return List.of(new ItemStack(item));
    }
}
