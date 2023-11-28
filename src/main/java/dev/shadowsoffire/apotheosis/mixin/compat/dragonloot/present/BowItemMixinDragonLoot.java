package dev.shadowsoffire.apotheosis.mixin.compat.dragonloot.present;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.shadowsoffire.placebo.Placebo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = BowItem.class, priority = 1500)
public class BowItemMixinDragonLoot {

    @TargetHandler(
            mixin = "net.dragonloot.mixin.BowItemMixin",
            name = "onStoppedUsingMixin"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "HEAD"), cancellable = true)
    private void zenith$CancelDamageIncreaseDragonLoot(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo originalCi, Player playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, AbstractArrow persistentProjectileEntity, CallbackInfo info) {
        info.cancel(); //Implemented via ProjectileDamageAttributes
    }
}
