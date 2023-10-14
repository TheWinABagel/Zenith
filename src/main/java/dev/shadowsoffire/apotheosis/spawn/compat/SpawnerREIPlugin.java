package dev.shadowsoffire.apotheosis.spawn.compat;

import com.google.common.collect.ImmutableMap;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.compat.EnchantingREICategory;
import dev.shadowsoffire.apotheosis.ench.compat.EnchantingREIDisplay;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.util.REIUtil;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.anvil.AnvilRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class SpawnerREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return Apotheosis.loc("spawner").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableEnch) return;
        registry.add(new SpawnerREICategory());
        registry.addWorkstations(SpawnerREIDisplay.ID, EntryIngredients.ofItemStacks(List.of(new ItemStack(Blocks.SPAWNER))));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableSpawner) return;
        registry.registerFiller(SpawnerModifier.class, SpawnerREIDisplay::new);
    }
}
