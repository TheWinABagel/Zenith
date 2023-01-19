package safro.zenith.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.EnchModule;
import safro.zenith.ench.enchantments.masterwork.GrowthSerumEnchant;

@Mixin(Sheep.class)
public abstract class SheepMixin {
    private DyeColor randomColor = null;

    @Shadow public abstract void shear(SoundSource soundSource);

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V"))
    private void apotheosisPreShear(Sheep sheep, SoundSource soundSource, Player player, InteractionHand interactionHand) {
        ItemStack shears = player.getItemInHand(interactionHand);
        if (Zenith.enableEnch && EnchantmentHelper.getItemEnchantmentLevel(EnchModule.CHROMATIC, shears) > 0) {
            randomColor = DyeColor.byId(sheep.getRandom().nextInt(16));
            shearRandom(randomColor);
        } else
            this.shear(soundSource);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V", shift = At.Shift.AFTER))
    private void apotheosisPostShear(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        Sheep sheep = (Sheep) (Object) this;
        ItemStack shears = player.getItemInHand(interactionHand);
        if (Zenith.enableEnch) {
            if (EnchantmentHelper.getItemEnchantmentLevel(EnchModule.EXPLOITATION, shears) > 0) {
                if (randomColor == null) {
                    this.shear(SoundSource.PLAYERS);
                } else {
                    shearRandom(randomColor);
                }
                sheep.hurt(DamageSource.GENERIC, 2);
            }
            randomColor = null;

            GrowthSerumEnchant.unshear(sheep, player.getItemInHand(interactionHand));
        }
    }

    private void shearRandom(DyeColor id) {
        Sheep sheep = (Sheep) (Object) this;
        sheep.level.playSound(null, sheep, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
        sheep.setSheared(true);
        int i = 1 + sheep.getRandom().nextInt(3);

        for(int j = 0; j < i; ++j) {
            ItemEntity itemEntity = sheep.spawnAtLocation(Sheep.ITEM_BY_DYE.get(id), 1);
            if (itemEntity != null) {
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((sheep.getRandom().nextFloat() - sheep.getRandom().nextFloat()) * 0.1F, sheep.getRandom().nextFloat() * 0.05F, (sheep.getRandom().nextFloat() - sheep.getRandom().nextFloat()) * 0.1F));
            }
        }
    }
}
