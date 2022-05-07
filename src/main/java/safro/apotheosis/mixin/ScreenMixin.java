package safro.apotheosis.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.apotheosis.util.ClientUtil;

@Mixin(Screen.class)
public class ScreenMixin {

    @Shadow @Nullable protected Minecraft minecraft;

    @Inject(method = "onClose", at = @At("HEAD"))
    private void apothPopScreen(CallbackInfo ci) {
        ClientUtil.popGuiLayer(this.minecraft);
    }
}
