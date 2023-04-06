package safro.zenith.mixin.anvil;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.EnchModule;

import java.util.Collections;
import java.util.List;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "onRemove", at = @At("HEAD"), cancellable = true)
    private void zenithOnRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (Zenith.enableEnch && state.is(BlockTags.ANVIL)) {
            if (!EnchModule.ANVIL_TILE.isValid(newState)) {
                world.removeBlockEntity(pos);
                ci.cancel();
            }
        }
    }

    @Inject(method = "getDrops", at = @At("HEAD"), cancellable = true)
    private void zenithGetDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (Zenith.enableEnch && state.is(BlockTags.ANVIL)) {
            cir.setReturnValue(Collections.emptyList());
        }
    }
}