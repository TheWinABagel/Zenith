package dev.shadowsoffire.apotheosis.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureContainerScreen;
import dev.shadowsoffire.apotheosis.util.DrawsOnLeft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements DrawsOnLeft {
    /**
     * Implements the slot highlight color for AdventureContainerScreen
     * */
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
    private void zenith$changeSlotHighlight(GuiGraphics guiGraphics, int x, int y, int blitOffset, Operation<Void> original) {
        if (!Apotheosis.enableAdventure || !((AbstractContainerScreen<?>) (Object) this instanceof AdventureContainerScreen<?> ad)) {
            original.call(guiGraphics, x, y, blitOffset);
            return;
        }
        zenith$renderSlotHighlight(guiGraphics, x, y, blitOffset, ad.getSlotColor(0));
    }

    private static void zenith$renderSlotHighlight(GuiGraphics guiGraphics, int x, int y, int blitOffset, int color) {
        guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, color, color, blitOffset);
    }
}
