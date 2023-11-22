package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.util.IShearHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ShearsDispenseItemBehavior.class)
public class ShearsDispenseItemBehaviorMixin  {
    @Unique
    private static ItemStack shears = ItemStack.EMPTY;

    @Inject(method = "execute",
            at = @At(value = "INVOKE", target = "net/minecraft/core/dispenser/ShearsDispenseItemBehavior.tryShearLivingEntity (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Z",
                    shift = At.Shift.BEFORE))
    private void captureItemStack(BlockSource source, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        shears = stack;
    }

    @Inject(method = "tryShearLivingEntity",
            at = @At(value = "INVOKE", target = "net/minecraft/world/entity/Shearable.shear (Lnet/minecraft/sounds/SoundSource;)V",
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void setShears(ServerLevel level, BlockPos pos, CallbackInfoReturnable<Boolean> cir, List list, Iterator var3, LivingEntity livingEntity, Shearable shearable) {
        if (shearable instanceof Sheep sheep){
            ((IShearHelper) sheep).zenithSetShears(shears);
        }
    }
}
