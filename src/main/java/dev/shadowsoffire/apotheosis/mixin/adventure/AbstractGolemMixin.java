package dev.shadowsoffire.apotheosis.mixin.adventure;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import net.minecraft.world.entity.animal.AbstractGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractGolem.class)
public class AbstractGolemMixin {
    @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
    private void zenith$fixBossesNotDespawning(double dist, CallbackInfoReturnable<Boolean> cir) {
        if (!Apotheosis.enableAdventure) return;
        AbstractGolem g = (AbstractGolem) (Object) this;
        if (g.tickCount > 12000 && ZenithComponents.BOSS_DATA.get(g).getIsBoss()) {
            int despawnDist = g.getType().getCategory().getDespawnDistance();
            int dsDistSq = despawnDist * despawnDist;
            if (dist > dsDistSq) {
                cir.setReturnValue(true);
            }
        }
    }
}
