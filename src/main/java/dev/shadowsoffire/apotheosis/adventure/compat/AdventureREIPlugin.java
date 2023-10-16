package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.util.GemIngredient;
import dev.shadowsoffire.apotheosis.util.REIUtil;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.category.extension.CategoryExtensionProvider;
import me.shedaniel.rei.api.client.registry.display.DisplayCategoryView;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.List;

public class AdventureREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return Apotheosis.loc("adventure").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableAdventure) return;
        registry.add(new SalvagingREICategory());
        registry.addWorkstations(SalvagingREIDisplay.ID, EntryIngredients.of(new ItemStack(Adventure.Blocks.SALVAGING_TABLE)));



    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
            if (stage != ReloadStage.END || !manager.equals(PluginManager.getClientInstance())) return;
            CategoryRegistry.getInstance().get(BuiltinPlugin.SMITHING).registerExtension(new AdventureREISmithingExtension());

    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableAdventure) return;
        registry.registerFiller(SalvagingRecipe.class, SalvagingREIDisplay::new);

        ItemStack gem = new ItemStack(dev.shadowsoffire.apotheosis.adventure.Adventure.Items.GEM);
        Gem gemObj = GemRegistry.INSTANCE.getRandomItem(new LegacyRandomSource(1854));
        GemItem.setGem(gem, gemObj);
        AffixHelper.setRarity(gem, gemObj.getMaxRarity());


        DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(EntryStacks.of(gem), Component.literal(""));
        info.line(Component.translatable("info.zenith.socketing"));
        registry.add(info);

        //REIUtil.addInfo(registry, gem.getItem(), "info.zenith.socketing");

        REIUtil.addInfo(registry, Adventure.Items.GEM_DUST, "info.zenith.gem_crushing");
        REIUtil.addInfo(registry, Adventure.Items.VIAL_OF_EXTRACTION, "info.zenith.gem_extraction");
        REIUtil.addInfo(registry, Adventure.Items.VIAL_OF_EXPULSION, "info.zenith.gem_expulsion");
        REIUtil.addInfo(registry, Adventure.Items.VIAL_OF_UNNAMING, "info.zenith.unnaming");


    }
}
