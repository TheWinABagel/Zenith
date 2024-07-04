package dev.shadowsoffire.apotheosis.adventure.affix;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.CachedObject;
import dev.shadowsoffire.placebo.util.CachedObject.CachedObjectSource;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AffixHelper {

    public static final ResourceLocation AFFIX_CACHED_OBJECT = Apotheosis.loc("affixes");

    public static final String DISPLAY = "display";
    public static final String LORE = "Lore";

    public static final String AFFIX_DATA = "affix_data";
    public static final String AFFIXES = "affixes";
    public static final String RARITY = "rarity";
    public static final String NAME = "name";
    // Used to encode the loot category of the shooting item on arrows.
    public static final String CATEGORY = "category";

    /**
     * Adds this specific affix to the Item's NBT tag.
     */
    public static void applyAffix(ItemStack stack, AffixInstance affix) {
        var affixes = new HashMap<>(getAffixes(stack));
        affixes.put(affix.affix(), affix);
        setAffixes(stack, affixes);
    }

    public static void setAffixes(ItemStack stack, Map<DynamicHolder<? extends Affix>, AffixInstance> affixes) {
        CompoundTag afxData = stack.getOrCreateTagElement(AFFIX_DATA);
        CompoundTag affixesTag = new CompoundTag();
        for (AffixInstance inst : affixes.values()) {
            affixesTag.putFloat(inst.affix().getId().toString(), inst.level());
        }
        afxData.put(AFFIXES, affixesTag);
    }

    public static void setName(ItemStack stack, Component name) {
        CompoundTag afxData = stack.getOrCreateTagElement(AFFIX_DATA);
        afxData.putString(NAME, Component.Serializer.toJson(name));
    }

    @Nullable
    public static Component getName(ItemStack stack) {
        if (!stack.hasTag()) return null;
        CompoundTag afxData = stack.getTagElement(AFFIX_DATA);
        if (afxData == null) return null;
        return Component.Serializer.fromJson(afxData.getString(NAME));
    }

    /**
     * Gets the affixes of an item. The returned map is immutable.
     * <p>
     * Due to potential reloads, it is possible for an affix instance to become unbound but still remain cached.
     *
     * @param stack The stack being queried.
     * @return An immutable map of all affixes on the stack, or an empty map if none were found.
     * @apiNote Prefer using {@link #streamAffixes(ItemStack)} where applicable, since invalid instances will be pre-filtered.
     */
    public static Map<DynamicHolder<? extends Affix>, AffixInstance> getAffixes(ItemStack stack) {
        if (AffixRegistry.INSTANCE.getValues().isEmpty()) return Collections.emptyMap(); // Don't enter getAffixesImpl if the affixes haven't loaded yet.
        return CachedObjectSource.getOrCreate(stack, AFFIX_CACHED_OBJECT, AffixHelper::getAffixesImpl, CachedObject.hashSubkey(AFFIX_DATA));
    }

    public static Map<DynamicHolder<? extends Affix>, AffixInstance> getAffixesImpl(ItemStack stack) {
        if (stack.isEmpty()) return Collections.emptyMap();
        Map<DynamicHolder<? extends Affix>, AffixInstance> map = new HashMap<>();
        CompoundTag afxData = stack.getTagElement(AFFIX_DATA);
        if (afxData != null && afxData.contains(AFFIXES)) {
            CompoundTag affixes = afxData.getCompound(AFFIXES);
            DynamicHolder<LootRarity> rarity = getRarity(afxData);
            if (!rarity.isBound()) rarity = RarityRegistry.getMinRarity();
            LootCategory cat = LootCategory.forItem(stack);
            for (String key : affixes.getAllKeys()) {
                DynamicHolder<Affix> affix = AffixRegistry.INSTANCE.holder(new ResourceLocation(key));
                if (!affix.isBound() || !affix.get().canApplyTo(stack, cat, rarity.get())) continue;
                float lvl = affixes.getFloat(key);
                map.put(affix, new AffixInstance(affix, stack, rarity, lvl));
            }
        }
        return Collections.unmodifiableMap(map);
    }

    public static Stream<AffixInstance> streamAffixes(ItemStack stack) {
        return getAffixes(stack).values().stream().filter(AffixInstance::isValid);
    }

    public static boolean hasAffixes(ItemStack stack) {
        return stack.hasTag() && !stack.getTag().getCompound(AFFIX_DATA).getCompound(AFFIXES).isEmpty();
    }

    @SuppressWarnings("NoTranslation")
    public static void setRarity(ItemStack stack, LootRarity rarity) {
        CompoundTag afxData = stack.getOrCreateTagElement(AFFIX_DATA);
        afxData.putString(RARITY, RarityRegistry.INSTANCE.getKey(rarity).toString());
    }

    public static void copyFrom(ItemStack stack, Entity entity) {
        if (hasAffixes(stack)) {
            CompoundTag afxData = stack.getTagElement(AFFIX_DATA);
            ZenithComponents.AFFIX_DATA.get(entity).setEntityTag(afxData.copy());
        }
    }

    @Nullable
    public static LootCategory getShooterCategory(Entity entity) {
        CompoundTag afxData = entity.getCustomData().getCompound(AFFIX_DATA);
        if (afxData != null && afxData.contains(CATEGORY)) {
            return LootCategory.byId(afxData.getString(CATEGORY));
        }
        return null;
    }

    public static Map<DynamicHolder<Affix>, AffixInstance> getAffixes(Entity entity) {
        Map<DynamicHolder<Affix>, AffixInstance> map = new HashMap<>();
        if (entity == null) return map;
        CompoundTag afxData = ZenithComponents.AFFIX_DATA.get(entity).getEntityTag();
        if (afxData != null && afxData.contains(AFFIXES)) {
            CompoundTag affixes = afxData.getCompound(AFFIXES);
            DynamicHolder<LootRarity> rarity = getRarity(afxData);
            if (!rarity.isBound()) rarity = RarityRegistry.getMinRarity();
            for (String key : affixes.getAllKeys()) {
                DynamicHolder<Affix> affix = AffixRegistry.INSTANCE.holder(new ResourceLocation(key));
                if (!affix.isBound()) continue;
                float lvl = affixes.getFloat(key);
                map.put(affix, new AffixInstance(affix, ItemStack.EMPTY, rarity, lvl));
            }
        }
        return map;
    }

    public static Stream<AffixInstance> streamAffixes(Entity entity) {
        return getAffixes(entity).values().stream();
    }

    /**
     * May be unbound
     */
    public static DynamicHolder<LootRarity> getRarity(ItemStack stack) {
        if (!stack.hasTag()) return RarityRegistry.INSTANCE.emptyHolder();
        CompoundTag afxData = stack.getTagElement(AFFIX_DATA);
        return getRarity(afxData);
    }

    /**
     * May be unbound
     */
    public static DynamicHolder<LootRarity> getRarity(@Nullable CompoundTag afxData) {
        if (afxData != null) {
            try {
                return RarityRegistry.byLegacyId(afxData.getString(RARITY));
            }
            catch (IllegalArgumentException e) {
                afxData.remove(RARITY);
                return RarityRegistry.byLegacyId("empty");
            }
        }
        return RarityRegistry.INSTANCE.emptyHolder();
    }

    /**
     * Helper method to add vanilla lore text to an item stack.
     *
     * @param stack The stack to add text to
     * @param lore  The text component to add
     */
    public static void addLore(ItemStack stack, Component lore) {
        CompoundTag display = stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
        ListTag tag = display.getList(ItemStack.TAG_LORE, 8);
        tag.add(StringTag.valueOf(Component.Serializer.toJson(lore)));
        display.put(ItemStack.TAG_LORE, tag);
    }

    public static Collection<DynamicHolder<Affix>> byType(AffixType type) {
        return AffixRegistry.INSTANCE.getTypeMap().get(type);
    }

    public static StepFunction step(float min, int steps, float step) {
        return new StepFunction(min, steps, step);
    }

}
