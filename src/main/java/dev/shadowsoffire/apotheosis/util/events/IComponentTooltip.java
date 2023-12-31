package dev.shadowsoffire.apotheosis.util.events;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

public interface IComponentTooltip {

    void zenith$RenderComponentTooltip(Font font, List<? extends FormattedText> tooltips, int mouseX, int mouseY);
}
