package dev.shadowsoffire.apotheosis.mixin.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.SugarCaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin {

    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 3))
    private int zenith$maxHeightSugarcane(int old) {
        return Apotheosis.enableGarden ? GardenModule.maxReedHeight : old;
    }
}

