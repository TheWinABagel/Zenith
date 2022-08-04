package safro.zenith.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.crafting.AbstractIngredient;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.stream.Stream;

public class CachedIngredient extends AbstractIngredient {
    private static final Int2ObjectMap<CachedIngredient> ingredients = new Int2ObjectOpenHashMap<>();
    private final boolean simple;

    private CachedIngredient(ItemStack... matches) {
        super(Arrays.stream(matches).map(ItemValue::new));
        if (matches.length == 1) ingredients.put(StackedContents.getStackingIndex(matches[0]), this);
        simple = Arrays.stream(values).noneMatch(list -> list.getItems().stream().anyMatch(stack -> stack.getItem().canBeDepleted()));
    }

    public static CachedIngredient create(ItemStack... matches) {
        synchronized (ingredients) {
            if (matches.length == 1) {
                CachedIngredient coi = ingredients.get(StackedContents.getStackingIndex(matches[0]));
                return coi != null ? coi : new CachedIngredient(matches);
            } else return new CachedIngredient(matches);
        }
    }

    @Override
    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonArray = new JsonArray();
            Ingredient.Value[] var2 = this.values;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Ingredient.Value value = var2[var4];
                jsonArray.add(value.serialize());
            }

            return jsonArray;
        }
    }

    @Override
    public IngredientDeserializer getDeserializer() {
        return VanillaSerializer.INSTANCE;
    }

    // Implementation of default vanilla serializer
    public static class VanillaSerializer implements IngredientDeserializer {
        public static final VanillaSerializer INSTANCE = new VanillaSerializer();

        @Override
        public Ingredient fromNetwork(FriendlyByteBuf buffer) {
            return Ingredient.fromValues(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()));
        }

        @Override
        public Ingredient fromJson(JsonObject json) {
            return Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(json)));
        }
    }
}
