package safro.zenith.mixin.garden;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.zenith.Zenith;
import safro.zenith.garden.GardenModule;

import java.util.Random;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {

    @Shadow @Final public static IntegerProperty AGE;

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void apothRandomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random, CallbackInfo ci) {
        SugarCaneBlock s = (SugarCaneBlock) (Object) this;
        if (Zenith.enableGarden) {
            if (worldIn.isEmptyBlock(pos.above())) {
                int i = 0;
                if (GardenModule.maxReedHeight <= 32) for (i = 1; worldIn.getBlockState(pos.below(i)).getBlock() == s; ++i)
                    ;

                if (i < GardenModule.maxReedHeight) {
                    int j = state.getValue(AGE);
                    if (j == 15) {
                        worldIn.setBlockAndUpdate(pos.above(), s.defaultBlockState());
                        worldIn.setBlock(pos, state.setValue(AGE, Integer.valueOf(0)), 4);
                    } else {
                        worldIn.setBlock(pos, state.setValue(AGE, Integer.valueOf(j + 1)), 4);
                    }
                }
            }

            ci.cancel();
        }
    }
}
