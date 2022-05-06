package safro.apotheosis.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.ench.enchantments.NaturesBlessingEnchant;
import safro.apotheosis.ench.enchantments.masterwork.ChainsawEnchant;
import safro.apotheosis.ench.enchantments.masterwork.EarthsBoonEnchant;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow protected ServerLevel level;

    @Shadow @Final protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void apotheosisBreakBlockEvent(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.level.getBlockState(blockPos);
        if (Apotheosis.enableEnch) {
            ChainsawEnchant.chainsaw(player, blockPos, state);
            EarthsBoonEnchant.provideBenefits(player, blockPos, state);
        }
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.BEFORE), cancellable = true)
    private void apotheosisRightClick(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (Apotheosis.enableEnch) {
            InteractionResult result = NaturesBlessingEnchant.rightClick(itemStack, player, level, blockHitResult.getBlockPos(), interactionHand);
            if (result != null) {
                cir.setReturnValue(result);
            }
        }
    }
}
