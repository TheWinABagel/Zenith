package dev.shadowsoffire.apotheosis.mixin.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.world.level.block.CactusBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(CactusBlock.class)
public class CactusBlockMixin {

    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 3))
    private int zenithModifyCactusHeight(int constant){
        if (Apotheosis.enableGarden){
            return GardenModule.maxCactusHeight;
        }
        return constant;
    }
}
