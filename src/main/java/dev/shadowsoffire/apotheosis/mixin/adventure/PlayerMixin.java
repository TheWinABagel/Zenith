package dev.shadowsoffire.apotheosis.mixin.adventure;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.util.Events;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract float getAttackStrengthScale(float adjustTicks);

    @Inject(method = "attack", at = @At("HEAD"))
    private void getAtkStrength(Entity target, CallbackInfo ci){
        Apotheosis.localAtkStrength = this.getAttackStrengthScale(0.5F);
    }

    @ModifyReturnValue(method = "hasCorrectToolForDrops", at = @At("RETURN"))
    private boolean addHarvestCheckEvent(boolean result, BlockState state){
        return result || Events.HarvestCheck.ATTEMPT_HARVEST.invoker().harvestAttempt(((Player) (Object) this), state);
    }
}
