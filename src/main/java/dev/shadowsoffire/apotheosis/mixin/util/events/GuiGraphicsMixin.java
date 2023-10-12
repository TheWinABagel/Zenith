package dev.shadowsoffire.apotheosis.mixin.util.events;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Optional;

@Mixin(value = GuiGraphics.class, priority = 2000)
public abstract class GuiGraphicsMixin {

    @Shadow
    public abstract int guiWidth();

    @Shadow
    public abstract int guiHeight();


    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.BEFORE))
    private void cacheItemStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (Apotheosis.enableAdventure) AdventureModuleClient.StackStorage.hoveredItem = itemStack;
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.AFTER))
    private void clearStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (Apotheosis.enableAdventure) AdventureModuleClient.StackStorage.hoveredItem = ItemStack.EMPTY;
    }

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"))
    private void gatherComponents(Args args, Font font, List<Component> lines, Optional<TooltipComponent> data, int x, int y) {
        if (Apotheosis.enableAdventure && SocketHelper.getSockets(AdventureModuleClient.StackStorage.hoveredItem) != 0) {
            args.set(1, AdventureModuleClient.gatherTooltipComponents(AdventureModuleClient.StackStorage.hoveredItem, lines, data, x, guiWidth(), guiHeight(), font));
        }
    }

}
