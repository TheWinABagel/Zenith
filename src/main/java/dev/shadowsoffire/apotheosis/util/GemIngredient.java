package dev.shadowsoffire.apotheosis.util;

import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class GemIngredient implements CustomIngredient {

    protected final DynamicHolder<LootRarity> rarity;
    protected ItemStack[] items;

    public GemIngredient(DynamicHolder<LootRarity> rarity) {
        super();
        this.rarity = rarity;
    }

    @Override
    public boolean test(ItemStack stack) {
        var rarity = AffixHelper.getRarity(stack);
        return stack.getItem() == Adventure.Items.GEM && rarity.isBound() && rarity == this.rarity;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        if (this.items == null) {
            Collection<Gem> gems = GemRegistry.INSTANCE.getValues();
            if (gems.size() == 0) return List.of(new ItemStack[0]); // Hasn't been initialized yet, don't cache.
            this.items = new ItemStack[gems.size()];
            int i = 0;
            for (Gem g : GemRegistry.INSTANCE.getValues()) {
                this.items[i++] = GemRegistry.createGemStack(g, this.rarity.get());
            }
        }
        return List.of(this.items);
    }

    @Override
    public boolean requiresTesting() {
        return false;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public LootRarity getRarity() {
        return this.rarity.get();
    }

    public static class Serializer implements CustomIngredientSerializer<GemIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ResourceLocation getIdentifier() {
            return Apotheosis.loc("gem");
        }

        @Override
        public GemIngredient read(JsonObject json) {
            var rarity = RarityRegistry.INSTANCE.holder(new ResourceLocation(GsonHelper.getAsString(json, "rarity")));
            return new GemIngredient(rarity);
        }

        @Override
        public void write(JsonObject json, GemIngredient ingredient) {

        }

        @Override
        public GemIngredient read(FriendlyByteBuf buf) {
            var rarity = RarityRegistry.INSTANCE.holder(buf.readResourceLocation());
            return new GemIngredient(rarity);
        }

        @Override
        public void write(FriendlyByteBuf buffer, GemIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.rarity.getId());
        }
    }

}
