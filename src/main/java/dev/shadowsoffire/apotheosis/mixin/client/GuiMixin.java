package dev.shadowsoffire.apotheosis.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {

    /**
     * @reason Extends the time the action bar message is set on the screen from 3 seconds to 8 seconds.
     */
    @ModifyExpressionValue(method = "setOverlayMessage", at = @At(value = "CONSTANT", args = "intValue=60"))
    public int zenith_extendTime(int old) {
        if (Apotheosis.enableAdventure) return 160;
        return old;
    }

}
