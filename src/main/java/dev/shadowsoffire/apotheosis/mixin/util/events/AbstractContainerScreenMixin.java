package dev.shadowsoffire.apotheosis.mixin.util.events;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Shadow
    protected Slot hoveredSlot;

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.BEFORE))
    private void collectTooltipItem(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci){
        if (Apotheosis.enableAdventure) AdventureModuleClient.StackStorage.hoveredItem = hoveredSlot.getItem();
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.AFTER))
    private void clearTooltipItem(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci){
        if (Apotheosis.enableAdventure) AdventureModuleClient.StackStorage.hoveredItem = ItemStack.EMPTY;
    }
}
