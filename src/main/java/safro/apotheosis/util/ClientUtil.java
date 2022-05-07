package safro.apotheosis.util;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.Stack;

// Should only be called on Client
public class ClientUtil {
    private static final Stack<Screen> guiLayers = new Stack<>();

    public static void resizeGuiLayers(Minecraft minecraft, int width, int height) {
        guiLayers.forEach(screen -> screen.resize(minecraft, width, height));
    }

    public static void clearGuiLayers(Minecraft minecraft) {
        while(guiLayers.size() > 0)
            popGuiLayerInternal(minecraft);
    }

    private static void popGuiLayerInternal(Minecraft minecraft) {
        if (minecraft.screen != null)
            minecraft.screen.removed();
        minecraft.screen = guiLayers.pop();
    }

    public static void pushGuiLayer(Minecraft minecraft, Screen screen) {
        if (minecraft.screen != null)
            guiLayers.push(minecraft.screen);
        minecraft.screen = Objects.requireNonNull(screen);
        screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        NarratorChatListener.INSTANCE.sayNow(screen.getNarrationMessage());

        ScreenEvents.beforeRender(screen).register(((layer, poseStack, mouseX, mouseY, partialTick) -> {
            layer.render(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);
            poseStack.translate(0,0,2000);
        }));
    }

    public static void popGuiLayer(Minecraft minecraft) {
        if (guiLayers.size() == 0) {
            minecraft.setScreen(null);
            return;
        }
        popGuiLayerInternal(minecraft);
        if (minecraft.screen != null)
            NarratorChatListener.INSTANCE.sayNow(minecraft.screen.getNarrationMessage());
    }

    public static float getGuiFarPlane() {
        return 1000.0F + 2000.0F * (1 + guiLayers.size());
    }

    public static boolean isActiveAndMatches(KeyMapping key, InputConstants.Key keyCode) {
        return key.matchesMouse(keyCode.getValue()) && key.isDown();
    }

    // For REI
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
