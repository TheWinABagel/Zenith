package safro.apotheosis.mixin.garden;

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
import safro.apotheosis.Apotheosis;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void apothSugarCaneNeighbor(BlockState state, Level world, BlockPos pos, Block block, BlockPos origin, boolean isMoving, CallbackInfo ci) {
        if (Apotheosis.enableGarden && state.is(Blocks.SUGAR_CANE)) {
            if (pos.getY() != origin.getY()) {
                return;
            } else {
                ci.cancel();
            }
        }
    }
}
