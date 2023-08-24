package dev.shadowsoffire.apotheosis.mixin.ench;

import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FishingHook.class)
public class FishingHookMixin {

    @ModifyVariable(method = "catchingFish", at = @At(value = "INVOKE",
            target = "net/minecraft/util/Mth.nextInt (Lnet/minecraft/util/RandomSource;II)I",
            ordinal = 2))
    private int test(FishingHook value) { // mcdev lying as usual
        return EnchHooks.getTicksCaughtDelay(value);
    }
}
