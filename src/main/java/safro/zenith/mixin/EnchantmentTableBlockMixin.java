package safro.zenith.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.table.ApothEnchantContainer;
import safro.zenith.ench.table.EnchantingStatManager;

import java.util.Random;

/**
 * Enchantment Tables were re-written with mixins instead of replacing the block entirely to improve compat
 */

@Mixin(EnchantmentTableBlock.class)
public class EnchantmentTableBlockMixin {

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    private void apothEnchMenu(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        if (Zenith.enableEnch) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof EnchantmentTableBlockEntity) {
                Component itextcomponent = ((Nameable) tileentity).getDisplayName();
                cir.setReturnValue(new SimpleMenuProvider((id, inventory, player) -> new ApothEnchantContainer(id, inventory, ContainerLevelAccess.create(world, pos)), itextcomponent));
            } else {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void apothEnchAnimate(BlockState blockState, Level level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
        if (Zenith.enableEnch) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    if (i > -2 && i < 2 && j == -1) {
                        j = 2;
                    }

                    if (rand.nextInt(16) == 0) {
                        for (int k = 0; k <= 1; ++k) {
                            BlockPos blockpos = pos.offset(i, k, j);
                            if (EnchantingStatManager.getEterna(level.getBlockState(blockpos), level, blockpos) > 0) {
                                if (!level.isEmptyBlock(pos.offset(i / 2, 0, j / 2))) {
                                    break;
                                }

                                level.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, i + rand.nextFloat() - 0.5D, k - rand.nextFloat() - 1.0F, j + rand.nextFloat() - 0.5D);
                            }
                        }
                    }
                }
            }
            ci.cancel();
        }
    }
}
