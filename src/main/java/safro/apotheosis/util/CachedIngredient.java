package safro.apotheosis.util;

import io.github.fabricators_of_create.porting_lib.crafting.IIngredientSerializer;
import io.github.fabricators_of_create.porting_lib.crafting.VanillaIngredientSerializer;
import io.github.fabricators_of_create.porting_lib.extensions.IngredientExtensions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public class CachedIngredient extends Ingredient implements IngredientExtensions {
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
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return VanillaIngredientSerializer.INSTANCE;
    }
}
