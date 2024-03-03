package dev.shadowsoffire.apotheosis.mixin.util.advancements;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.shadowsoffire.apotheosis.advancements.TrueItemPredicate;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Locale;
import java.util.Map;

@Mixin(InventoryChangeTrigger.class)
public class InventoryChangeTriggerMixin {

    @ModifyArgs(method = "createInstance(Lcom/google/gson/JsonObject;Lnet/minecraft/advancements/critereon/ContextAwarePredicate;Lnet/minecraft/advancements/critereon/DeserializationContext;)Lnet/minecraft/advancements/critereon/InventoryChangeTrigger$TriggerInstance;",
            at = @At(value = "INVOKE", target = "net/minecraft/advancements/critereon/InventoryChangeTrigger$TriggerInstance.<init> (Lnet/minecraft/advancements/critereon/ContextAwarePredicate;Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;[Lnet/minecraft/advancements/critereon/ItemPredicate;)V"))
    private void test(Args args, JsonObject json, ContextAwarePredicate predicate, DeserializationContext deserializationContext) {
        if (json.has("zenith")) {
            args.set(4 , deserializeZenith(json.getAsJsonObject("zenith")));
        }
    }

    @Unique
    private ItemPredicate[] deserializeZenith(JsonObject json) {
        String type = json.get("type").getAsString();
        if ("spawn_egg".equals(type)) return new ItemPredicate[] { new TrueItemPredicate(s -> s.getItem() instanceof SpawnEggItem) };
        if ("enchanted".equals(type)) {
            Enchantment ench = json.has("enchantment") ? BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(json.get("enchantment").getAsString())) : null;
            MinMaxBounds.Ints bound = MinMaxBounds.Ints.fromJson(json.get("level"));
            return new ItemPredicate[] { new TrueItemPredicate(s -> {
                Map<Enchantment, Integer> enchMap = EnchantmentHelper.getEnchantments(s);
                if (ench != null) return bound.matches(enchMap.getOrDefault(ench, 0));
                return enchMap.values().stream().anyMatch(bound::matches);
            }) };
        }
        if ("affix".equals(type)) {
            return new ItemPredicate[] { new TrueItemPredicate(s -> !AffixHelper.getAffixes(s).isEmpty()) };
        }
        if ("rarity".equals(type)) {
            var rarity = RarityRegistry.byLegacyId(json.get("rarity").getAsString().toLowerCase(Locale.ROOT));
            return new ItemPredicate[] { new TrueItemPredicate(s -> !AffixHelper.getAffixes(s).isEmpty() && rarity.isBound() && AffixHelper.getRarity(s) == rarity) };
        }
        if ("gem_rarity".equals(type)) {
            var rarity = RarityRegistry.byLegacyId(json.get("rarity").getAsString().toLowerCase(Locale.ROOT));
            return new ItemPredicate[] { new TrueItemPredicate(s -> s.getItem() == Adventure.Items.GEM && rarity.isBound() && AffixHelper.getRarity(s) == rarity) };
        }
        if ("socket".equals(type)) {
            return new ItemPredicate[] { new TrueItemPredicate(s -> SocketHelper.getGems(s).stream().anyMatch(gem -> !gem.isEmpty())) };
        }
        if ("nbt".equals(type)) {
            CompoundTag tag;
            try {
                tag = TagParser.parseTag(GsonHelper.convertToString(json.get("nbt"), "nbt"));
            }
            catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            return new ItemPredicate[] { new TrueItemPredicate(s -> {
                if (!s.hasTag()) return false;
                for (String key : tag.getAllKeys()) {
                    if (!tag.get(key).equals(s.getTag().get(key))) return false;
                }
                return true;
            }) };

        }
        return new ItemPredicate[0];
    }
}
