package safro.zenith.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.resources.ResourceLocation;
import safro.zenith.Zenith;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModuleCondition implements ConditionJsonProvider {
    public static ResourceLocation ID = new ResourceLocation(Zenith.MODID, "module");

    static Map<String, Supplier<Boolean>> types = new HashMap<>();
    static {
        types.put("spawner", () -> Zenith.enableSpawner);
        types.put("garden", () -> Zenith.enableGarden);
//        types.put("deadly", () -> Zenith.enableAdventure);
        types.put("enchantment", () -> Zenith.enableEnch);
        types.put("potion", () -> Zenith.enablePotion);
        types.put("village", () -> Zenith.enableVillage);
        types.put("book", () -> Zenith.giveBook);
    }

    @Override
    public ResourceLocation getConditionId() {
        return ID;
    }

    @Override
    public void writeParameters(JsonObject json) {
        json.addProperty("module", json.get("module").getAsString());
    }

    public static boolean test(JsonObject json) {
        String element = json.get("module").getAsString();
        if (!json.has("module") || !types.containsKey(element)) throw new JsonParseException("Invalid module condition!");
        return types.get(element).get();
    }
}
