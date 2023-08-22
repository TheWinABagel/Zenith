package dev.shadowsoffire.apotheosis.mixin.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.SugarCaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;


@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {

    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 3))
    private int zenithModifySugarCaneHeight(int constant){
        if (Apotheosis.enableGarden){
            return GardenModule.maxReedHeight;
        }
        return constant;
    }

}

