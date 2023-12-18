package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingBlock;
import dev.shadowsoffire.apotheosis.compat.ZenithREICatgeory;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class GemCuttingCategory  extends ZenithREICatgeory<GemCuttingDisplay> {

    public static final ResourceLocation TEXTURES = new ResourceLocation(Apotheosis.MODID, "textures/gui/gem_cutting_jei.png");

    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(TEXTURES, bounds.getX() + this.getXOffset(), bounds.getY(), 0, 0, 148, 78);
    }

    @Override
    public int getDisplayHeight() {
        return 78;
    }

    @Override
    public void draw(GemCuttingDisplay display, Point origin, double mouseX, double mouseY, GuiGraphics gfx) {}

    @Override
    public void setRecipe(GemCuttingDisplay display, List<Widget> widgets, Point origin) {
        var recipe = display.getRecipe();
            widgets.add(slot(46, 14, origin, List.of(EntryStacks.of(recipe.gem)), false));
            widgets.add(slot(5, 14, origin, List.of(EntryStacks.of(recipe.dust)), false));
            widgets.add(slot(46, 57, origin, List.of(EntryStacks.of(recipe.gem)), false));
            widgets.add(slot(87, 14, origin, Arrays.stream(recipe.materials).map(EntryStacks::of).toList(), false));
            widgets.add(slot(129, 14, origin, List.of(EntryStacks.of(recipe.out)), false));
    }

    @Override
    public CategoryIdentifier<? extends GemCuttingDisplay> getCategoryIdentifier() {
        return GemCuttingDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return GemCuttingBlock.NAME;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Adventure.Blocks.GEM_CUTTING_TABLE);
    }
}
