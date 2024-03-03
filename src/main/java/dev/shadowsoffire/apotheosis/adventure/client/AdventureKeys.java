package dev.shadowsoffire.apotheosis.adventure.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.net.RadialStateChangeMessage;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class AdventureKeys {

    public static final KeyMapping TOGGLE_RADIAL = new KeyMapping("key." + Apotheosis.MODID + ".toggle_radial_mining", Type.KEYSYM, GLFW.GLFW_KEY_O,
            "key.categories." + Apotheosis.MODID);

    public static void registerKeys() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_RADIAL);
    }

    public static void handleKeys() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Minecraft.getInstance().player == null) return;

            while (TOGGLE_RADIAL.consumeClick()) {
                if (Minecraft.getInstance().screen == null) {
                    ClientPlayNetworking.send(new RadialStateChangeMessage());
                }
            }
        });
    }
}