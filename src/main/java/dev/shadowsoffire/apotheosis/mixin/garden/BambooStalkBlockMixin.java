package dev.shadowsoffire.apotheosis.mixin.garden;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.BambooStalkBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {

    @ModifyExpressionValue(method = "getHeightAboveUpToMax", at = @At(value = "CONSTANT", args = "intValue=16"))
    private int zenith$ModifyHeightAbove(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyExpressionValue(method = "getHeightBelowUpToMax",  at = @At(value = "CONSTANT", args = "intValue=16"))
    private int zenith$ModifyHeightBelow(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyExpressionValue(method = "randomTick",  at = @At(value = "CONSTANT", args = "intValue=16"))
    private int zenith$ModifyBambooHeight(int old){
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyExpressionValue(method = "isValidBonemealTarget",  at = @At(value = "CONSTANT", args = "intValue=16"))
    private int zenithModifyBambooValidBonemeal(int old){
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyExpressionValue(method = "performBonemeal",  at = @At(value = "CONSTANT", args = "intValue=16"))
    private int zenithModifyBambooPerformBonemeal(int old){
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyExpressionValue(method = "growBamboo",  at = @At(value = "CONSTANT", args = "intValue=11"))
    private int zenith$growOne(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyExpressionValue(method = "growBamboo",  at = @At(value = "CONSTANT", args = "intValue=15"))
    private int zenith$growTwo(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }
}
