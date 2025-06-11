package dev.shadowsoffire.apotheosis.mixin.garden;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.SugarCaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin {

    @ModifyExpressionValue(method = "randomTick",  at = @At(value = "CONSTANT", args = "intValue=3"))
    private int zenith$maxHeightSugarcane(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxReedHeight : old;
    }
}

