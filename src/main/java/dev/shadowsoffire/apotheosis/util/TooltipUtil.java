package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.mixin.TooltipRenderUtilAccessor;
import net.minecraft.client.gui.GuiGraphics;

public class TooltipUtil { //yet another shameless yoink
    public static void renderTooltipBackground(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight, int pZ, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        int i = pX - 3;
        int j = pY - 3;
        int k = pWidth + 3 + 3;
        int l = pHeight + 3 + 3;
        TooltipRenderUtilAccessor.callRenderHorizontalLine(pGuiGraphics, i, j - 1, k, pZ, backgroundTop);
        TooltipRenderUtilAccessor.callRenderHorizontalLine(pGuiGraphics, i, j + l, k, pZ, backgroundBottom);
        renderRectangle(pGuiGraphics, i, j, k, l, pZ, backgroundTop, backgroundBottom);
        TooltipRenderUtilAccessor.callRenderVerticalLineGradient(pGuiGraphics, i - 1, j, l, pZ, backgroundTop, backgroundBottom);
        TooltipRenderUtilAccessor.callRenderVerticalLineGradient(pGuiGraphics, i + k, j, l, pZ, backgroundTop, backgroundBottom);
        TooltipRenderUtilAccessor.callRenderFrameGradient(pGuiGraphics, i, j + 1, k, l, pZ, borderTop, borderBottom);
    }

    private static void renderRectangle(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight, int pZ, int pColor, int colorTo) {
        pGuiGraphics.fillGradient(pX, pY, pX + pWidth, pY + pHeight, pZ, pColor, colorTo);
    }
}
