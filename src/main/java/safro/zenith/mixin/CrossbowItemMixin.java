package safro.zenith.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import safro.zenith.Zenith;
import safro.zenith.ench.enchantments.masterwork.CrescendoEnchant;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "use", at = @At(value = "RETURN", ordinal = 0))
    public void zenithAddCharges(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
        if (Zenith.enableEnch) {
            CrescendoEnchant.onArrowFired(pPlayer.getItemInHand(pHand));
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;performShooting(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;FF)V"))
    public void zenithPreFired(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
        if (Zenith.enableEnch) {
            CrescendoEnchant.preArrowFired(pPlayer.getItemInHand(pHand));
        }
    }

    @Inject(method = "shootProjectile", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/CrossbowItem;getArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void zenithMarkArrows(Level pLevel, LivingEntity pShooter, InteractionHand pHand, ItemStack pCrossbowStack, ItemStack pAmmoStack, float pSoundPitch, boolean pIsCreativeMode, float pVelocity, float pInaccuracy, float pProjectileAngle, CallbackInfo ci, boolean flag, Projectile arrow) {
        if (Zenith.enableEnch) {
            CrescendoEnchant.markGeneratedArrows(arrow, pCrossbowStack);
        }
    }
}
