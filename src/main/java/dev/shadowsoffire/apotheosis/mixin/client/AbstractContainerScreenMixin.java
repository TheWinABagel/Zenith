package dev.shadowsoffire.apotheosis.mixin.client;

import dev.shadowsoffire.apotheosis.util.DrawsOnLeft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements DrawsOnLeft {

}
