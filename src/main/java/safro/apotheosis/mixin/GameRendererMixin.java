package safro.apotheosis.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.util.ScreenUtil;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private Minecraft minecraft;

   /* @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"))
    private void apothScreenRender(Screen screen, PoseStack poseStack, int i, int j, float f) {
        if (Apotheosis.enableEnch) {
            ScreenUtil.drawScreen(screen, poseStack, i, j, this.minecraft.getDeltaFrameTime());
        } else
            screen.render(poseStack, i, j, this.minecraft.getDeltaFrameTime());
    } */

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Matrix4f;orthographic(FFFFFF)Lcom/mojang/math/Matrix4f;"), index = 5)
    private float apothGuiFar(float f) {
        return ScreenUtil.getGuiFarPlane();
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"), index = 2)
    private double apothGuiFar1(double d) {
        return 1000F - ScreenUtil.getGuiFarPlane();
    }
}
