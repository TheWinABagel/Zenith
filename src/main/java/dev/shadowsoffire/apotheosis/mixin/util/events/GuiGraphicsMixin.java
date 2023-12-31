package dev.shadowsoffire.apotheosis.mixin.util.events;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient;
import dev.shadowsoffire.apotheosis.util.events.IComponentTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(value = GuiGraphics.class, priority = 1000)
public abstract class GuiGraphicsMixin implements IComponentTooltip {

    @Shadow
    public abstract int guiWidth();

    @Shadow
    public abstract int guiHeight();

    @Shadow
    private void renderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner) {}

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.BEFORE))
    private void zenith$cacheItemStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (Apotheosis.enableAdventure) AdventureModuleClient.StackStorage.hoveredItem = itemStack;
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", shift = At.Shift.AFTER))
    private void zenith$clearStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        if (Apotheosis.enableAdventure) AdventureModuleClient.StackStorage.hoveredItem = ItemStack.EMPTY;
    }

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"))
    private void zenith$gatherComponents(Args args, Font font, List<Component> lines, Optional<TooltipComponent> data, int x, int y) {
        if (Apotheosis.enableAdventure && SocketHelper.getSockets(AdventureModuleClient.StackStorage.hoveredItem) != 0) {
            var list = new ArrayList<>(AdventureModuleClient.gatherTooltipComponents(AdventureModuleClient.StackStorage.hoveredItem, lines, data, x, guiWidth(), guiHeight(), font));
            args.set(1, list);
        }
    }

    @Unique
    @Override
    public void zenith$RenderComponentTooltip(Font font, List<? extends FormattedText> tooltips, int mouseX, int mouseY) {
        var components = AdventureModuleClient.gatherTooltipComponents(ItemStack.EMPTY, tooltips, Optional.empty(), mouseX, guiWidth(), guiHeight(), font);
        renderTooltipInternal(font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
    }
}
