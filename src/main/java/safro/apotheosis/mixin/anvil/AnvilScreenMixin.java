package safro.apotheosis.mixin.anvil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;

@Mixin(value = AnvilScreen.class, priority = 999)
public class AnvilScreenMixin {

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40, ordinal = 0))
    public int apoth_removeLevelCap(int old) {
        return Integer.MAX_VALUE;
    }

}
