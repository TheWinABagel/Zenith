package dev.shadowsoffire.apotheosis.potion.compat;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiLog;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;

public class PotionCharmEMIRecipe extends EmiCraftingRecipe {

    public PotionCharmEMIRecipe(ShapedRecipe recipe, ItemStack potion) {
        super(padIngredients(recipe, potion), EmiStack.of(getOutputCharm(potion)), getPotionId(recipe, potion), false);
        setRemainders(input, recipe);
    }

    public static void setRemainders(List<EmiIngredient> input, CraftingRecipe recipe) {
        try {
            TransientCraftingContainer inv = EmiUtil.getCraftingInventory();
            for (int i = 0; i < input.size(); i++) {
                if (input.get(i).isEmpty()) {
                    continue;
                }
                for (int j = 0; j < input.size(); j++) {
                    if (j == i) {
                        continue;
                    }
                    if (!input.get(j).isEmpty()) {
                        inv.setItem(j, input.get(j).getEmiStacks().get(0).getItemStack().copy());
                    }
                }
                List<EmiStack> stacks = input.get(i).getEmiStacks();
                for (EmiStack stack : stacks) {
                    inv.setItem(i, stack.getItemStack().copy());
                    ItemStack remainder = recipe.getRemainingItems(inv).get(i);
                    if (!remainder.isEmpty()) {
                        stack.setRemainder(EmiStack.of(remainder));
                    }
                }
                inv.clearContent();
            }
        } catch (Exception e) {
            EmiLog.error("Exception thrown setting remainders for " + EmiPort.getId(recipe));
            e.printStackTrace();
        }
    }

    private static List<EmiIngredient> padIngredients(ShapedRecipe recipe, ItemStack stack) {
        List<EmiIngredient> list = Lists.newArrayList();
        int i = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x >= recipe.getWidth() || y >= recipe.getHeight() || i >= recipe.getIngredients().size()) {
                    list.add(EmiStack.EMPTY);
                } else {
                    Ingredient ing = recipe.getIngredients().get(i);
                    if (ing.test(stack)) {
                        list.add(EmiStack.of(stack));
                    }
                    else {
                        list.add(EmiIngredient.of(ing));
                    }
                    i++;
                }
            }
        }
        return list;
    }

    private static ResourceLocation getPotionId(ShapedRecipe recipe, ItemStack stack) {
        return new ResourceLocation(recipe.getId() + "_" + BuiltInRegistries.POTION.getKey(PotionUtils.getPotion(stack)).getPath());
    }

    private static ItemStack getOutputCharm(ItemStack potionStack) {
        ItemStack charm = new ItemStack(PotionModule.POTION_CHARM);
        Potion potion = PotionUtils.getPotion(potionStack);
        PotionUtils.setPotion(charm, potion);
        return charm;
    }
}
