package safro.zenith.ench.table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import safro.zenith.util.ApotheosisUtil;

public class KeepNBTEnchantingRecipe extends EnchantingRecipe {
    public static final Serializer SERIALIZER = new Serializer();

    public KeepNBTEnchantingRecipe(ResourceLocation id, ItemStack output, Ingredient input, EnchantingStatManager.Stats requirements, EnchantingStatManager.Stats maxRequirements) {
        super(id, output, input, requirements, maxRequirements);
    }

    public ItemStack assemble(ItemStack input, float eterna, float quanta, float arcana) {
        ItemStack out = this.getResultItem().copy();
        if (input.hasTag()) out.setTag(input.getTag().copy());
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KeepNBTEnchantingRecipe.SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<KeepNBTEnchantingRecipe> {
        protected static final Gson GSON = new GsonBuilder().create();

        @Override
        public KeepNBTEnchantingRecipe fromJson(ResourceLocation id, JsonObject obj) {
            ItemStack output = ApotheosisUtil.getItemStack(obj.get("result").getAsJsonObject(), true, true);
            Ingredient input = Ingredient.fromJson(obj.get("input"));
            EnchantingStatManager.Stats stats = GSON.fromJson(obj.get("requirements"), EnchantingStatManager.Stats.class);
            EnchantingStatManager.Stats maxStats = obj.has("max_requirements") ? GSON.fromJson(obj.get("max_requirements"), EnchantingStatManager.Stats.class) : NO_MAX;
            if (maxStats.eterna != -1 && stats.eterna > maxStats.eterna) throw new JsonParseException("An enchanting recipe (" + id + ") has invalid min/max eterna bounds (min > max).");
            if (maxStats.quanta != -1 && stats.quanta > maxStats.quanta) throw new JsonParseException("An enchanting recipe (" + id + ") has invalid min/max quanta bounds (min > max).");
            if (maxStats.arcana != -1 && stats.arcana > maxStats.arcana) throw new JsonParseException("An enchanting recipe (" + id + ") has invalid min/max arcana bounds (min > max).");
            return new KeepNBTEnchantingRecipe(id, output, input, stats, maxStats);
        }

        @Override
        public KeepNBTEnchantingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ItemStack output = buf.readItem();
            Ingredient input = Ingredient.fromNetwork(buf);
            EnchantingStatManager.Stats stats = EnchantingStatManager.Stats.read(buf);
            EnchantingStatManager.Stats maxStats = buf.readBoolean() ? EnchantingStatManager.Stats.read(buf) : NO_MAX;
            return new KeepNBTEnchantingRecipe(id, output, input, stats, maxStats);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, KeepNBTEnchantingRecipe recipe) {
            buf.writeItem(recipe.output);
            recipe.input.toNetwork(buf);
            recipe.requirements.write(buf);
            buf.writeBoolean(recipe.maxRequirements != NO_MAX);
            if (recipe.maxRequirements != NO_MAX) {
                recipe.maxRequirements.write(buf);
            }
        }

    }

}
