package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.mixin.accessors.ScreenAccessor;
import dev.shadowsoffire.apotheosis.util.events.IComponentTooltip;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement this on a screen class to be able to call {@link #drawOnLeft(GuiGraphics, List, int)}
 */
public interface DrawsOnLeft {

    /**
     * Renders a list of text as a tooltip attached to the left edge of the currently open container screen.
     */
    default void drawOnLeft(GuiGraphics gfx, List<Component> list, int y) {
        if (list.isEmpty()) return;
        int xPos = ((AbstractContainerScreenAccessor) ths()).getLeftPos() - 16 - list.stream().map(((ScreenAccessor) ths()).getFont()::width).max(Integer::compare).get();
        int maxWidth = 9999;
        if (xPos < 0) {
            maxWidth = ((AbstractContainerScreenAccessor) ths()).getLeftPos() - 6;
            xPos = -8;
        }

        List<FormattedText> split = new ArrayList<>();
        int lambdastupid = maxWidth;
        list.forEach(comp -> split.addAll(((ScreenAccessor) ths()).getFont().getSplitter().splitLines(comp, lambdastupid, comp.getStyle())));

        ((IComponentTooltip) gfx).zenithRenderComponentTooltip(((ScreenAccessor) ths()).getFont(), split, xPos, y); // copying forge methods is my passion
    }

    default AbstractContainerScreen<?> ths() {
        return (AbstractContainerScreen<?>) this;
    }

    public static void draw(AbstractContainerScreen<?> screen, GuiGraphics gfx, List<Component> list, int y) {
        ((DrawsOnLeft) screen).drawOnLeft(gfx, list, y);
    }

}
