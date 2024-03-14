package dev.shadowsoffire.apotheosis.mixin.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.BambooStalkBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {

    @ModifyConstant(method = "getHeightAboveUpToMax", constant = @Constant(intValue = 16))
    private int zenith$ModifyHeightAbove(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyConstant(method = "getHeightBelowUpToMax", constant = @Constant(intValue = 16))
    private int zenith$ModifyHeightBelow(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 16))
    private int zenith$ModifyBambooHeight(int old){
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyConstant(method = "isValidBonemealTarget", constant = @Constant(intValue = 16))
    private int zenithModifyBambooValidBonemeal(int old){
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyConstant(method = "performBonemeal", constant = @Constant(intValue = 16))
    private int zenithModifyBambooPerformBonemeal(int old){
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyConstant(method = "growBamboo", constant = @Constant(intValue = 11))
    private int zenith$growOne(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }

    @ModifyConstant(method = "growBamboo", constant = @Constant(intValue = 15))
    private int zenith$growTwo(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxBambooHeight : old;
    }
}
