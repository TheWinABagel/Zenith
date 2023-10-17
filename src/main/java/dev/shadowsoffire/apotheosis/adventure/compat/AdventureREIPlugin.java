package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.architectury.event.EventResult;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingMenu;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.util.GemIngredient;
import dev.shadowsoffire.apotheosis.util.REIUtil;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.visibility.DisplayVisibilityPredicate;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdventureREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return Apotheosis.loc("adventure").toString();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {


    }



    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        //registry
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableAdventure) return;
        registry.add(new SalvagingREICategory());
        registry.addWorkstations(SalvagingREIDisplay.ID, EntryIngredients.of(new ItemStack(Adventure.Blocks.SALVAGING_TABLE)));
        registry.add(new GemCuttingCategory());
        registry.addWorkstations(GemCuttingDisplay.ID, EntryIngredients.of(new ItemStack(Adventure.Blocks.GEM_CUTTING_TABLE)));
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (!Apotheosis.enableAdventure) return;
        if (stage != ReloadStage.END || !manager.equals(PluginManager.getClientInstance())) return;
    //    CategoryRegistry.getInstance().get(BuiltinPlugin.SMITHING).registerExtension(new AdventureREISmithingExtension());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableAdventure) return;
        registry.registerFiller(SalvagingRecipe.class, SalvagingREIDisplay::new);
        registry.registerFiller(GemCuttingDisplay.GemCuttingRecipe.class, GemCuttingDisplay::new);

        List<Ingredient> gemlist = new ArrayList<>();
        RarityRegistry.INSTANCE.getValues().forEach(lootRarity -> gemlist.add(new GemIngredient(RarityRegistry.INSTANCE.holder(lootRarity)).toVanilla()));

        DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntries(EntryIngredients.ofIngredient(gemlist.get(0)), Component.literal(""));
        info.line(Component.translatable("info.zenith.socketing"));
        registry.add(info);

        REIUtil.addInfo(registry, Adventure.Items.GEM_DUST, "info.zenith.gem_crushing");
        REIUtil.addInfo(registry, Adventure.Items.VIAL_OF_EXTRACTION, "info.zenith.gem_extraction");
        REIUtil.addInfo(registry, Adventure.Items.VIAL_OF_EXPULSION, "info.zenith.gem_expulsion");
        REIUtil.addInfo(registry, Adventure.Items.VIAL_OF_UNNAMING, "info.zenith.unnaming");

        //List<GemCuttingDisplay.GemCuttingRecipe> gemCutRecipes = new ArrayList<>();

        for (Gem g : GemRegistry.INSTANCE.getValues()) {
            LootRarity r = RarityRegistry.getMinRarity().get();
            LootRarity max = RarityRegistry.getMaxRarity().get();
            while (r != max) {
                if (g.clamp(r) == r) registry.add(new GemCuttingDisplay.GemCuttingRecipe(g, r));
                r = r.next();
            }
        }
        //registry.add();


        registry.registerVisibilityPredicate(new HideSmithingRecipes());
    }

    private static class HideSmithingRecipes implements DisplayVisibilityPredicate { // This is done as the recipes don't render correctly

        @Override
        public EventResult handleDisplay(DisplayCategory<?> category, Display display) {
            var origin = DisplayRegistry.getInstance().getDisplayOrigin(display);
            if (origin instanceof Recipe<?> recipe && recipe.getId().getNamespace().equals("zenith")){
                return switch (recipe.getId().getPath()) {
                    case "unnaming" -> EventResult.interruptFalse();
                    case "socketing" -> EventResult.interruptFalse();
                    case "extraction" -> EventResult.interruptFalse();
                    case "expulsion" -> EventResult.interruptFalse();
                    case "add_sockets" -> EventResult.interruptFalse();
                    case "superior_add_sockets" -> EventResult.interruptFalse();
                    default -> EventResult.pass();
                };
            }
            return EventResult.pass();
        }
    }
}
