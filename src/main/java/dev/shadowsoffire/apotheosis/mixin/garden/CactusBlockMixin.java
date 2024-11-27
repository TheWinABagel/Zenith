package dev.shadowsoffire.apotheosis.mixin.garden;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.CactusBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin {

    @ModifyExpressionValue(method = "randomTick",  at = @At(value = "CONSTANT", args = "intValue=3"))
    private int zenithModifyCactusHeight(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxCactusHeight : old;
    }
}
