package safro.apotheosis.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.apotheosis.util.ScreenUtil;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Final private Window window;

    @Inject(method = "resizeDisplay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;resize(Lnet/minecraft/client/Minecraft;II)V", shift = At.Shift.AFTER))
    private void apothScreenResize(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        ScreenUtil.resizeGuiLayers(mc, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;reset()V", shift = At.Shift.AFTER))
    private void apothScreenClear(Screen screen, CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        ScreenUtil.clearGuiLayers(mc);
    }
}
