package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.util.events.IComponentTooltip;
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

    default void drawOnLeft(GuiGraphics gfx, List<Component> list, int y) {
        if (list.isEmpty()) return;
        int xPos = ths().leftPos - 16 - list.stream().map(ths().font::width).max(Integer::compare).get();
        int maxWidth = 9999;
        if (xPos < 0) {
            maxWidth = ths().leftPos - 6;
            xPos = -8;
        }

        List<FormattedText> split = new ArrayList<>();
        int lambdastupid = maxWidth;
        list.forEach(comp -> split.addAll(ths().font.getSplitter().splitLines(comp, lambdastupid, comp.getStyle())));

        ((IComponentTooltip) gfx).zenithRenderComponentTooltip(ths().font, split, xPos, y); // copying forge methods is my passion
    }

    default AbstractContainerScreen<?> ths() {
        return (AbstractContainerScreen<?>) this;
    }

}
