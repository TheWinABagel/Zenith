package dev.shadowsoffire.apotheosis.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.inventories.BedrockAnvilScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(BedrockAnvilScreen.class)
public class BedrockAnvilScreenMixin {
}
