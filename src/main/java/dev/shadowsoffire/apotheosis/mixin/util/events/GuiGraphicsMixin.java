package dev.shadowsoffire.apotheosis.mixin.util.events;

import com.mojang.datafixers.util.Either;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient;
import dev.shadowsoffire.apotheosis.adventure.client.SocketTooltipRenderer;
import dev.shadowsoffire.apotheosis.util.events.ModifyComponents;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.message.Message;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Optional;

@Mixin(value = GuiGraphics.class, priority = 1100)
public abstract class GuiGraphicsMixin {

    @Shadow
    public abstract int guiWidth();

    @Shadow
    public abstract int guiHeight();
    @Unique
    private ItemStack stack = ItemStack.EMPTY;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void saveStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        stack = itemStack;
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
    private void clearStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        stack = ItemStack.EMPTY;
    }

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"))
    private void gatherComponents(Args args, Font font, List<Component> lines, Optional<TooltipComponent> data, int x, int y) {
        if (Apotheosis.enableAdventure && SocketHelper.getSockets(stack) != 0) {
            AdventureModuleClient.gatherTooltipComponents(stack, lines, data, x, guiWidth(), guiHeight(), font);
        }
    }

}
