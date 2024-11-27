package dev.shadowsoffire.apotheosis.spawn.compat;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.compat.ZenithEMIPlugin;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModule;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class SpawnerEMIPlugin {

    public static EmiRecipeCategory SPAWNER = new EmiRecipeCategory(Apotheosis.loc("spawner"),
            EmiStack.of(Blocks.SPAWNER), ZenithEMIPlugin.simplifiedRenderer(0, 0), EmiRecipeSorting.compareInputThenOutput());

    public static void register(EmiRegistry registry) {
        registry.addCategory(SPAWNER);
        registry.addWorkstation(SPAWNER, EmiStack.of(Blocks.SPAWNER));

        for (SpawnerModifier recipe : registry.getRecipeManager().getAllRecipesFor(SpawnerModule.MODIFIER)) {
            ZenithEMIPlugin.addRecipeSafe(registry, () -> new SpawnerEMIRecipe(recipe), recipe);
        }

        if (SpawnerModule.spawnerSilkLevel == -1) {
            ZenithEMIPlugin.addRecipeSafe(registry, () ->
                    new EmiInfoRecipe(List.of(EmiStack.of(Blocks.SPAWNER)), List.of(Component.translatable("info.zenith.spawner.no_silk")), Apotheosis.syntheticLoc("no_silk_info")));
        }
        else if (SpawnerModule.spawnerSilkLevel == 0) {
            ZenithEMIPlugin.addRecipeSafe(registry, () ->
                    new EmiInfoRecipe(List.of(EmiStack.of(Blocks.SPAWNER)), List.of(Component.translatable("info.zenith.spawner.always_drop")), Apotheosis.syntheticLoc("always_drop_info")));
        }
        else ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(List.of(EmiStack.of(Blocks.SPAWNER)), List.of(Component.translatable("info.zenith.spawner", Enchantments.SILK_TOUCH.getFullname(SpawnerModule.spawnerSilkLevel))), Apotheosis.syntheticLoc("spawner_info")));
        List<Ingredient> eggList = new ArrayList<>();
        for (Item i : BuiltInRegistries.ITEM) {
            if (i instanceof SpawnEggItem) {
                eggList.add(Ingredient.of(i));
            }
        }
        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(eggList.stream().map(EmiIngredient::of).toList(), List.of(Component.translatable("info.zenith.capturing")), Apotheosis.syntheticLoc("spawn_egg_info")));


    }
}
