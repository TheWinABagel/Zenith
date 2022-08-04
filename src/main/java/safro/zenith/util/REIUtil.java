package safro.zenith.util;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.ItemLike;

// Should only be called on Client
public class REIUtil {

    public static void addInfo(DisplayRegistry registry, ItemLike entry, String text) {
        DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(EntryStacks.of(entry), new TextComponent(""));
        info.line(new TranslatableComponent(text));
        registry.add(info);
    }

    public static void addInfo(DisplayRegistry registry, ItemLike entry, Component text) {
        DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(EntryStacks.of(entry), new TextComponent(""));
        info.line(text);
        registry.add(info);
    }
}
