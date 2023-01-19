package safro.zenith.mixin.garden;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CactusBlock;
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

@Mixin(CactusBlock.class)
public class CactusBlockMixin {

    @Shadow @Final public static IntegerProperty AGE;

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void apothRandomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        CactusBlock cactus = (CactusBlock) (Object) this;
        if (Zenith.enableGarden) {
            BlockPos blockpos = pos.above();
            if (!world.isOutsideBuildHeight(blockpos) && world.isEmptyBlock(blockpos)) {
                int i = 1;

                if (GardenModule.maxCactusHeight <= 32) for (; world.getBlockState(pos.below(i)).getBlock() == cactus; ++i)
                    ;

                if (i < GardenModule.maxCactusHeight) {
                    int j = state.getValue(AGE);
                    if (j == 15) {
                        world.setBlockAndUpdate(blockpos, cactus.defaultBlockState());
                        BlockState iblockstate = state.setValue(AGE, Integer.valueOf(0));
                        world.setBlock(pos, iblockstate, 4);
                        iblockstate.neighborChanged(world, blockpos, cactus, pos, false);
                    } else {
                        world.setBlock(pos, state.setValue(AGE, Integer.valueOf(j + 1)), 4);
                    }
                }
            }

            ci.cancel();
        }
    }
}
