package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EnchantmentScreen.class)
public interface EnchantmentScreenAccessor {
    @Invoker
    void callRenderBook(GuiGraphics guiGraphics, int x, int y, float partialTick);
}
