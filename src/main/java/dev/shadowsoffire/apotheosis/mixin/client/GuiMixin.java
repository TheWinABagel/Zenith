package dev.shadowsoffire.apotheosis.mixin.client;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Gui.class)
public class GuiMixin {

    /**
     * @reason Extends the time the action bar message is set on the screen from 3 seconds to 8 seconds.
     */
    @ModifyConstant(method = "setOverlayMessage")
    public int zenith_extendTime(int old) {
        if (Apotheosis.enableAdventure) return 160;
        return old;
    }

}
