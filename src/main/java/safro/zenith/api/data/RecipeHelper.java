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
import safro.zenith.Apoth;
import safro.zenith.Zenith;
import safro.zenith.api.RunnableReloader;
import safro.zenith.util.CachedIngredient;

import java.util.*;

public class RecipeHelper {
    private static final List<Recipe<?>> recipes = new ArrayList<>();

    protected String modid;
    protected Set<String> names = new HashSet<>();

    public RecipeHelper(String modid) {
        this.modid = modid;
    }

    public static void addRecipe(Recipe<?> rec) {
        synchronized (recipes) {
            if (rec == null) {
                Zenith.LOGGER.error("Attempted to add null recipe, this is invalid behavior.");
                Thread.dumpStack();
            }
            recipes.add(rec);
        }
    }

    public void addShapeless(Object output, Object... inputs) {
        ItemStack out = makeStack(output);
        addRecipe(new ShapelessRecipe(this.name(out), this.modid, out, this.createInput(false, inputs)));
    }

    public void addShaped(Object output, int width, int height, Object... input) {
        addRecipe(this.genShaped(makeStack(output), width, height, input));
    }

    public ShapedRecipe genShaped(ItemStack output, int l, int w, Object... input) {
        if (l * w != input.length) throw new UnsupportedOperationException("Attempted to add invalid shaped recipe.  Complain to the author of " + this.modid);
        return new ShapedRecipe(this.name(output), this.modid, l, w, this.createInput(true, input), output);
    }

    public static NonNullList<Ingredient> createInput(boolean allowEmpty, Object... input) {
        NonNullList<Ingredient> inputL = NonNullList.create();
        for (int i = 0; i < input.length; i++) {
            Object k = input[i];
            if (k instanceof String) inputL.add(i, Ingredient.of(Apoth.registerItem(new ResourceLocation((String) k))));
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

    public void addSimpleShapeless(Object output, Object input, int numInputs) {
        this.addShapeless(output, NonNullList.withSize(numInputs, makeStack(input)).toArray(new Object[0]));
    }

    private ResourceLocation name(ItemStack out) {
        String name = Registry.ITEM.getKey(out.getItem()).getPath();
        while (this.names.contains(name)) {
            name += "_";
        }
        this.names.add(name);
        return new ResourceLocation(this.modid, name);
    }

    public static ItemStack makeStack(Object thing) {
        return makeStack(thing, 1);
    }

    static void addRecipes(RecipeManager mgr) {
        recipes.forEach(r -> {
            Map<ResourceLocation, Recipe<?>> map = mgr.recipes.computeIfAbsent(r.getType(), t -> new HashMap<>());
            Recipe<?> old = map.get(r.getId());
            if (old == null) {
                map.put(r.getId(), r);
            } else Zenith.LOGGER.debug("Skipping registration for code recipe {} as a json recipe already exists with that ID.", r.getId());
        });
        Zenith.LOGGER.info("Registered {} additional recipes.", recipes.size());
    }

    public static void mutableManager(RecipeManager mgr) {
        mgr.recipes = new HashMap<>(mgr.recipes);
        for (RecipeType<?> type : mgr.recipes.keySet()) {
            mgr.recipes.put(type, new HashMap<>(mgr.recipes.get(type)));
        }
    }

    public static PreparableReloadListener getReloader(RecipeManager mgr) {
        return RunnableReloader.of(() -> {
            mutableManager(mgr);
            addRecipes(mgr);
        }, "recipe_reloader");
    }
}