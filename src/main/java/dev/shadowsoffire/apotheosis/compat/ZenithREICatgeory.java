package dev.shadowsoffire.apotheosis.compat;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ZenithREICatgeory<T extends Display> implements DisplayCategory<T> {

    @Override
    public List<Widget> setupDisplay(T display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        if (getBackground(bounds) != null) {
            widgets.add(getBackground(bounds));
        } else {
            widgets.add(Widgets.createRecipeBase(bounds));
        }
        Point p = new Point(bounds.getX() + this.getXOffset(), bounds.getY() + this.getYOffset());
        widgets.add(Widgets.createDrawableWidget((helper, mouseX, mouseY, partialTick) -> {
            draw(display, p, mouseX, mouseY, helper);
        }));
        setRecipe(display, widgets, p);
        return widgets;
    }

    public abstract void draw(T display, Point origin, double mouseX, double mouseY, GuiGraphics gfx);

    public abstract void setRecipe(T display, List<Widget> widgets, Point origin);

    public Widget getBackground(Rectangle bounds) {
        return null;
    }

    public int getXOffset() {
        return 0;
    }

    public int getYOffset() {
        return 0;
    }

    public static Slot slot(int x, int y, Point p, Collection<? extends EntryStack<?>> stacks, boolean background) {
        if (!background) return Widgets.createSlot(new Point(p.getX() + x, p.getY() + y)).entries(stacks).disableBackground();
        return Widgets.createSlot(new Point(p.getX() + x, p.getY() + y)).entries(stacks);
    }
}
