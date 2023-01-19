package safro.zenith.util;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import safro.zenith.Zenith;
import safro.zenith.api.enchant.TableApplicableEnchant;
import safro.zenith.api.enchant.TableApplicableItem;
import safro.zenith.ench.EnchModuleEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ApotheosisUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ItemStack getRandom(TagKey<Item> tag, RandomSource random) {
        ItemStack stack = ItemStack.EMPTY;
        stack = Registry.ITEM.getTag(tag).flatMap((items) -> items.getRandomElement((RandomSource) random)).map((itemEntry) -> (itemEntry.value()).getDefaultInstance()).orElse(stack);
        return stack;
    }

    public static int getLootingLevel(DamageSource source) {
        if (source.getEntity() instanceof Player p) {
            return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, p.getMainHandItem());
        }
        return 0;
    }

    // Equivalent to stack.canApplyAtEnchantingTable(ench);
    public static boolean canApplyItem(Enchantment ench, ItemStack stack) {
        return ((TableApplicableItem)stack.getItem()).canApplyAtEnchantingTable(stack, ench);
    }

    // Equivalent to ench.canApplyAtEnchantingTable(stack);
    public static boolean canApplyEnchantment(Enchantment ench, ItemStack stack) {
        return ((TableApplicableEnchant)ench).canApplyAtEnchantingTable(stack);
    }

    public static boolean isAllowedOnBooks(Enchantment ench) {
        return ((TableApplicableEnchant)ench).isAllowedOnBooks();
    }

    public static boolean anvilChanged(AnvilMenu container, ItemStack left, ItemStack right, Container outputSlot, String name, int baseCost, Player player) {
        ItemStack output;
        int cost = 0;
        int materialCost = 0;

        // Definitely not cursed
        Pair<ItemStack, List<Integer>> results = EnchModuleEvents.anvilEvent(left, right, player, cost, materialCost);
        output = results.getFirst();
        cost = results.getSecond().get(0);
        materialCost = results.getSecond().get(1);

        if (output.isEmpty() || !Zenith.enableEnch) {
            return true;
        }

        outputSlot.setItem(0, output);
        container.cost.set(cost);
        container.repairItemCountCost = materialCost;
        return false;
    }

    public static boolean processConditions(JsonObject json, String memberName) {
        return !json.has(memberName) || processConditions(GsonHelper.getAsJsonArray(json, memberName));
    }

    public static boolean processConditions(JsonArray conditions) {
        for (int x = 0; x < conditions.size(); x++) {
            if (!conditions.get(x).isJsonObject())
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");

            JsonObject json = conditions.get(x).getAsJsonObject();
            if (!ResourceConditions.objectMatchesConditions(json))
                return false;
        }
        return true;
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
        String itemName = GsonHelper.getAsString(json, "item");
        ResourceLocation itemKey = new ResourceLocation(itemName);

        if (!Registry.ITEM.containsKey(itemKey))
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        Item item = Registry.ITEM.get(itemKey);

        if (disallowsAirInRecipe && item == Items.AIR)
            throw new JsonSyntaxException("Invalid item: " + itemName);

        if (readNBT && json.has("nbt")) {
            try {
                JsonElement element = json.get("nbt");
                CompoundTag nbt;
                if(element.isJsonObject())
                    nbt = TagParser.parseTag(GSON.toJson(element));
                else
                    nbt = TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));

                CompoundTag tmp = new CompoundTag();
                if (nbt.contains("ForgeCaps")) {
                    tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
                    nbt.remove("ForgeCaps");
                }

                tmp.put("tag", nbt);
                tmp.putString("id", itemName);
                tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

                return ItemStack.of(tmp);
            }
            catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
            }
        }

        return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
    }

    static boolean late = false;
    static Map<ResourceLocation, RecipeType<?>> unregisteredTypes = new HashMap<>();

    public static <T extends Recipe<?>> RecipeType<T> makeRecipeType(final String pIdentifier) {
        if (late) throw new RuntimeException("Attempted to register a recipe type after the registration period closed.");
        RecipeType<T> type = new RecipeType<T>() {
            public String toString() {
                return pIdentifier;
            }
        };
        unregisteredTypes.put(new ResourceLocation(pIdentifier), type);
        return type;
    }

    public static void registerTypes() {
        unregisteredTypes.forEach((key, type) -> Registry.register(Registry.RECIPE_TYPE, key, type));
        Zenith.LOGGER.debug("Registered {} recipe types.", unregisteredTypes.size());
        unregisteredTypes.clear();
        late = true;
    }

    public static boolean canApply(Enchantment ench, ItemStack stack) {
        if (stack.getItem() instanceof ShearsItem) {
            return canApplyEnchantment(ench, stack) || ench == Enchantments.UNBREAKING || ench == Enchantments.BLOCK_EFFICIENCY || ench == Enchantments.BLOCK_FORTUNE;
        } else if (stack.getItem() instanceof TridentItem) {
            return canApplyEnchantment(ench, stack) || ench == Enchantments.SHARPNESS || ench == Enchantments.MOB_LOOTING || ench == Enchantments.PIERCING;
        }
        return canApplyEnchantment(ench, stack);
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) return 0;
        if (level <= 15) return sum(level, 7, 2);
        if (level <= 30) return 315 + sum(level - 15, 37, 5);
        return 1395 + sum(level - 30, 112, 9);
    }

    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    public static int getLevelForExperience(int targetXp) {
        int level = 0;
        while (true) {
            final int xpToNextLevel = xpBarCap(level);
            if (targetXp < xpToNextLevel) return level;
            level++;
            targetXp -= xpToNextLevel;
        }
    }

    public static int xpBarCap(int level) {
        if (level >= 30) return 112 + (level - 30) * 9;

        if (level >= 15) return 37 + (level - 15) * 5;

        return 7 + level * 2;
    }
}
