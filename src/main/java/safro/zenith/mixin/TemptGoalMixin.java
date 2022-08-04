package safro.zenith.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.enchantments.TemptingEnchant;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {

    @Inject(method = "shouldFollow", at = @At(value = "RETURN"), cancellable = true)
    public void apoth_tempting(LivingEntity entity, CallbackInfoReturnable<Boolean> ci) {
        if (Zenith.enableEnch && !ci.getReturnValueZ() && TemptingEnchant.shouldFollow(entity)) ci.setReturnValue(true);
    }

}
