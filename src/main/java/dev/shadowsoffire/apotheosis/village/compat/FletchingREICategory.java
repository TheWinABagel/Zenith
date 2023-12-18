package dev.shadowsoffire.apotheosis.village.compat;

import dev.shadowsoffire.apotheosis.compat.ZenithREICatgeory;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingScreen;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.List;

public class FletchingREICategory extends ZenithREICatgeory<FletchingREIDisplay> {


    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(FletchingScreen.TEXTURES, bounds.getX(), bounds.getY(), 6, 16, 139, 54);
    }

    @Override
    public int getDisplayWidth(FletchingREIDisplay display) {
        return 139;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public void draw(FletchingREIDisplay display, Point origin, double mouseX, double mouseY, GuiGraphics gfx) {}

    @Override
    public void setRecipe(FletchingREIDisplay display, List<Widget> widgets, Point origin) {
        FletchingRecipe recipe = display.getRecipe();
        List<List<ItemStack>> inputs = recipe.getIngredients().stream().map(i -> Arrays.asList(i.getItems())).toList();
        for (int i = 0; i < 3; i++) {
            widgets.add(slot(42, 1 + i * 18, origin, inputs.get(i).stream().map(EntryStacks::of).toList(), false).markInput());
        }
        widgets.add(slot(117, 19, origin, List.of(EntryStacks.of(recipe.getOutput())), false).markOutput());
    }

    @Override
    public CategoryIdentifier<? extends FletchingREIDisplay> getCategoryIdentifier() {
        return FletchingREIDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("zenith.recipes.fletching");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.FLETCHING_TABLE);
    }
}
