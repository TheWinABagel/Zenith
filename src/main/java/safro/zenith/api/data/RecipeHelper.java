package safro.zenith.api.data;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import safro.zenith.Zenith;
import safro.zenith.api.RunnableReloader;
import safro.zenith.util.CachedIngredient;

import java.util.*;

public class RecipeHelper {
    public static NonNullList<Ingredient> createInput(boolean allowEmpty, Object... input) {
        NonNullList<Ingredient> inputL = NonNullList.create();
        for (int i = 0; i < input.length; i++) {
            Object k = input[i];
            if (k instanceof String) inputL.add(i, Ingredient.of(Zenith.registerItem(new ResourceLocation((String) k))));
            else if (k instanceof ItemStack && !((ItemStack) k).isEmpty()) inputL.add(i, CachedIngredient.create((ItemStack) k));
            else if (k instanceof ItemLike || k instanceof ItemStack) inputL.add(i, CachedIngredient.create(makeStack(k)));
            else if (k instanceof Ingredient) inputL.add(i, (Ingredient) k);
            else if (allowEmpty) inputL.add(i, Ingredient.EMPTY);
            else throw new UnsupportedOperationException("Attempted to add invalid recipe.  Complain to the author of " + Zenith.MODID + ". (Input " + k + " not allowed.)");
        }
        return inputL;
    }

    public static ItemStack makeStack(Object thing, int size) {
        if (thing instanceof ItemStack) return (ItemStack) thing;
        if (thing instanceof Item) return new ItemStack((Item) thing, size);
        if (thing instanceof Block) return new ItemStack((Block) thing, size);
        throw new IllegalArgumentException("Attempted to create an ItemStack from something that cannot be converted: " + thing);
    }

    public static ItemStack makeStack(Object thing) {
        return makeStack(thing, 1);
    }
}
