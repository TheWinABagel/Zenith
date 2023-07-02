package safro.zenith.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import safro.zenith.api.data.RecipeHelper;


import java.util.ArrayList;
import java.util.List;
/*
public class PotionShapedRecipe extends ShapedRecipe {

    final int width;
    final int height;
    protected final IntList potionSlots = new IntArrayList();
    final ItemStack result;
    private final ResourceLocation id;
    final Potion potion;


    public PotionShapedRecipe(ResourceLocation id, int width, int height, List<Object> ingredients, ItemStack result, Potion potion) {
        super(id, "", width, height, makeIngredients(ingredients), result);
        this.id = id;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
        this.result = result;
        this.potion = potion;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PotionShapedRecipe.Serializer.INSTANCE;
    }

    private static NonNullList<Ingredient> makeIngredients(List<Object> ingredients) {

        List<Object> realIngredients = new ArrayList<>(ingredients);

        return RecipeHelper.createInput(true, realIngredients.toArray());
    }


    public static class Serializer implements RecipeSerializer<PotionShapedRecipe> {

        public static final PotionShapedRecipe.Serializer INSTANCE = new PotionShapedRecipe.Serializer();

        @Override
        public PotionShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray inputs = json.get("recipe").getAsJsonArray();
            String potionString = json.get("potion").getAsString();
            Potion potion = Registry.POTION.get(new ResourceLocation(potionString));
            int width = 0, height = inputs.size();
            List<Object> ingredients = new ArrayList<>();
            for (JsonElement e : inputs) {
                JsonArray arr = e.getAsJsonArray();
                width = arr.size();
                for (JsonElement input : arr) {
                    if (input.isJsonPrimitive() && input.getAsString().equals("potion")) ingredients.add(potion);
                    else ingredients.add(Ingredient.fromJson(input));
                }
            }
            NonNullList<Ingredient> realIngredients = RecipeHelper.createInput(true, ingredients);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new PotionShapedRecipe(id, width, height, realIngredients, result, potion);
        }

        @Override
        public PotionShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buf) {
            int width = buf.readByte();
            int height = buf.readByte();
            int potions = buf.readByte();
            IntList potionSlots = new IntArrayList();
            for (int i = 0; i < potions; i++) {
                potionSlots.add(buf.readByte());
            }

            List<Object> inputs = new ArrayList<>(width * height);

            for (int i = 0; i < width * height; i++) {
                if (!potionSlots.contains(i)) inputs.add(i, Ingredient.fromNetwork(buf));
                else inputs.add("potion");
            }

            return new PotionShapedRecipe(inputs, width, height);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PotionShapedRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buf);
            }
            buf.writeItem(recipe.result);
        }

    }

}
*/