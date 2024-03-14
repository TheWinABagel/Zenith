package dev.shadowsoffire.apotheosis.mixin.garden;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {

    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void zenith$onSugarCaneUpdate(BlockState state, Level world, BlockPos pos, Block block, BlockPos origin, boolean isMoving, CallbackInfo ci) {
        if (state.is(Blocks.SUGAR_CANE)) {
            if (pos.getY() != origin.getY()) {
                ci.cancel();
            }
        }
    }
}
