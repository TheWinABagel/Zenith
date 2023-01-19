package safro.zenith.mixin.garden;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.garden.GardenModule;

import java.util.Random;

@Mixin(BambooBlock.class)
public abstract class BambooBlockMixin {

    @Shadow @Final public static IntegerProperty STAGE;

    @Shadow protected abstract int getHeightBelowUpToMax(BlockGetter blockGetter, BlockPos blockPos);

    @Shadow protected abstract int getHeightAboveUpToMax(BlockGetter blockGetter, BlockPos blockPos);

    @Shadow @Final public static EnumProperty<BambooLeaves> LEAVES;

    @Shadow @Final public static IntegerProperty AGE;

    @Shadow protected abstract void growBamboo(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource, int i);

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void apothRandomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (Zenith.enableGarden) {
            if (state.getValue(STAGE) == 0) {
                if (random.nextInt(3) == 0 && worldIn.isEmptyBlock(pos.above()) && worldIn.getRawBrightness(pos.above(), 0) >= 9) {
                    int i = this.getHeightBelowUpToMax(worldIn, pos) + 1;
                    if (i < GardenModule.maxBambooHeight) {
                        this.growBamboo(state, worldIn, pos, random, i);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    private void apothIsValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient, CallbackInfoReturnable<Boolean> cir) {
        if (Zenith.enableGarden) {
            int i = this.getHeightAboveUpToMax(worldIn, pos);
            int j = this.getHeightBelowUpToMax(worldIn, pos);
            cir.setReturnValue(i + j + 1 < GardenModule.maxBambooHeight && worldIn.getBlockState(pos.above(i)).getValue(STAGE) != 1);
        }
    }

    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void apothPerformBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if (Zenith.enableGarden) {
            int bambooAbove = this.getHeightAboveUpToMax(worldIn, pos);
            int bambooBelow = this.getHeightBelowUpToMax(worldIn, pos);
            int bambooSize = bambooAbove + bambooBelow + 1;
            int l = 1 + rand.nextInt(2);

            for (int i1 = 0; i1 < l; ++i1) {
                BlockPos blockpos = pos.above(bambooAbove);
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (bambooSize >= GardenModule.maxBambooHeight || blockstate.getValue(STAGE) == 1 || !worldIn.isEmptyBlock(blockpos.above())) {
                    return;
                }
                this.growBamboo(blockstate, worldIn, blockpos, rand, bambooSize);
                ++bambooAbove;
                ++bambooSize;
            }

            ci.cancel();
        }
    }

    @Inject(method = "getHeightAboveUpToMax", at = @At("HEAD"), cancellable = true)
    private void apothGetHeightAbove(BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (Zenith.enableGarden) {
            int i;
            for (i = 0; i < GardenModule.maxBambooHeight && worldIn.getBlockState(pos.above(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {
                ;
            }
            cir.setReturnValue(i);
        }
    }

    @Inject(method = "growBamboo", at = @At("HEAD"), cancellable = true)
    private void apothGrow(BlockState blockStateIn, Level worldIn, BlockPos posIn, RandomSource rand, int size, CallbackInfo ci) {
        BambooBlock b = (BambooBlock) (Object) this;
        if (Zenith.enableGarden) {
            BlockState blockstate = worldIn.getBlockState(posIn.below());
            BlockPos blockpos = posIn.below(2);
            BlockState blockstate1 = worldIn.getBlockState(blockpos);
            BambooLeaves bambooleaves = BambooLeaves.NONE;
            if (size >= 1) {
                if (blockstate.getBlock() == Blocks.BAMBOO && blockstate.getValue(LEAVES) != BambooLeaves.NONE) {
                    if (blockstate.getBlock() == Blocks.BAMBOO && blockstate.getValue(LEAVES) != BambooLeaves.NONE) {
                        bambooleaves = BambooLeaves.LARGE;
                        if (blockstate1.getBlock() == Blocks.BAMBOO) {
                            worldIn.setBlock(posIn.below(), blockstate.setValue(LEAVES, BambooLeaves.SMALL), 3);
                            worldIn.setBlock(blockpos, blockstate1.setValue(LEAVES, BambooLeaves.NONE), 3);
                        }
                    }
                } else {
                    bambooleaves = BambooLeaves.SMALL;
                }
            }

            int i = blockStateIn.getValue(AGE) != 1 && blockstate1.getBlock() != Blocks.BAMBOO ? 0 : 1;
            int j = (size < GardenModule.maxBambooHeight - GardenModule.maxBambooHeight / 5D || !(rand.nextFloat() < 0.25F)) && size != GardenModule.maxBambooHeight - 1 ? 0 : 1;
            worldIn.setBlock(posIn.above(), b.defaultBlockState().setValue(AGE, Integer.valueOf(i)).setValue(LEAVES, bambooleaves).setValue(STAGE, Integer.valueOf(j)), 3);

            ci.cancel();
        }
    }
}
