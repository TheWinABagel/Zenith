package dev.shadowsoffire.apotheosis.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 1500)
public class PortLibMixinSquared {
/*
        @TargetHandler(
                mixin = "io.github.fabricators_of_create.porting_lib.entity.mixin.PlayerMixin",
                name = "addKnockbackAttribute"
        )
        @Inject(method = "@MixinSquared:Handler",
                at = @At(value = "HEAD"), cancellable = true)
        private static void reduceLogLevel(AttributeSupplier.Builder builder, CallbackInfoReturnable cir) {
                cir.setReturnValue(builder);
        }*/


}
