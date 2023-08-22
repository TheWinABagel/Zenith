package dev.shadowsoffire.apotheosis.mixin.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {

    @Inject(method = "getHeightAboveUpToMax", at = @At("HEAD"), cancellable = true)
    private void zenithGetHeightAbove(BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (Apotheosis.enableGarden) {
            int i;
            for (i = 0; i < GardenModule.maxBambooHeight && worldIn.getBlockState(pos.above(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {;}
            cir.setReturnValue(i);
        }
    }

    @Inject(method = "getHeightBelowUpToMax", at = @At("HEAD"), cancellable = true)
    private void zenithGetHeightBelow(BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (Apotheosis.enableGarden) {
            int i;
            for (i = 0; i < GardenModule.maxBambooHeight && worldIn.getBlockState(pos.below(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {;}
            cir.setReturnValue(i);
        }
    }

    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 16))
    private int zenithModifyBambooHeight(int constant){
        if (Apotheosis.enableGarden){
            return GardenModule.maxBambooHeight;
        }
        return constant;
    }

    @ModifyConstant(method = "isValidBonemealTarget", constant = @Constant(intValue = 16))
    private int zenithModifyBambooValidBonemeal(int constant){
        if (Apotheosis.enableGarden){
            return GardenModule.maxBambooHeight;
        }
        return constant;
    }

    @ModifyConstant(method = "performBonemeal", constant = @Constant(intValue = 16))
    private int zenithModifyBambooPerformBonemeal(int constant){
        if (Apotheosis.enableGarden){
            return GardenModule.maxBambooHeight;
        }
        return constant;
    }

    @ModifyConstant(method = "growBamboo", constant = @Constant(intValue = 11))
    private int zenithGrowOne(int constant) {
        if (Apotheosis.enableGarden){
            return GardenModule.maxBambooHeight;
        }
        return constant;
    }

    @ModifyConstant(method = "growBamboo", constant = @Constant(intValue = 15))
    private int zenithGrowTwo(int constant) {
        if (Apotheosis.enableGarden){
            return GardenModule.maxBambooHeight;
        }
        return constant;
    }

}
