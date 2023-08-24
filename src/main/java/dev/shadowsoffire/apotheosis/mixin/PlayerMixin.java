package dev.shadowsoffire.apotheosis.mixin;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract float getAttackStrengthScale(float adjustTicks);

    @Inject(method = "attack", at = @At("HEAD"))
    private void getAtkStrength(Entity target, CallbackInfo ci){
        Apotheosis.localAtkStrength = this.getAttackStrengthScale(0.5F);
    }
}
