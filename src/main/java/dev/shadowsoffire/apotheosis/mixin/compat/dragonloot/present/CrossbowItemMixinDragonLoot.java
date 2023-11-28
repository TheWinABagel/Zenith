package dev.shadowsoffire.apotheosis.mixin.compat.dragonloot.present;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.shadowsoffire.placebo.Placebo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = CrossbowItem.class, priority = 1500)
public class CrossbowItemMixinDragonLoot {

    @TargetHandler(
            mixin = "net.dragonloot.mixin.CrossbowItemMixin",
            name = "createArrowMixin"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "HEAD"), cancellable = true)
    private static void zenith$CancelDamageIncreaseDragonLoot(Level world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<AbstractArrow> originalCi, ArrowItem arrowItem, AbstractArrow persistentProjectileEntity, CallbackInfo info) {
        info.cancel(); //Implemented via ProjectileDamageAttributes
    }

}
