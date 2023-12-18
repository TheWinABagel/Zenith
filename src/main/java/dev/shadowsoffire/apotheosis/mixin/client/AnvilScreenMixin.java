package dev.shadowsoffire.apotheosis.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.util.DrawsOnLeft;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

    @ModifyExpressionValue(method = "renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At(value = "CONSTANT", args = "intValue=40"))
    public int zenith$removeLevelCap(int old) {
        if (Apotheosis.enableEnch) return Integer.MAX_VALUE;
        return old;
    }

    @Inject(method = "renderFg", at = @At("TAIL"))
    private void test(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci){
        AnvilScreen anv = (AnvilScreen) (Object) this;
        int level = anv.getMenu().getCost();
        if (level <= 0 || !anv.getMenu().getSlot(anv.getMenu().getResultSlot()).hasItem()) return;
        List<Component> list = new ArrayList<>();
        list.add(Component.literal(I18n.get("info.zenith.anvil_at", level)).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GREEN));
        int expCost = EnchantmentUtils.getTotalExperienceForLevel(level);
        list.add(Component.translatable("info.zenith.anvil_xp_cost", Component.literal("" + expCost).withStyle(ChatFormatting.GREEN),
                Component.literal("" + level).withStyle(ChatFormatting.GREEN)));
        DrawsOnLeft.draw(anv, guiGraphics, list, ((AbstractContainerScreenAccessor) anv).getTopPos() + 28);
    }

}
