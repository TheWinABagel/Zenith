package dev.shadowsoffire.apotheosis.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    @ModifyExpressionValue(method = "renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At(value = "CONSTANT", args = "intValue=40"))
    public int zenith_removeLevelCap(int old) {
        if (Apotheosis.enableEnch) return Integer.MAX_VALUE;
        return old;
    }

}
