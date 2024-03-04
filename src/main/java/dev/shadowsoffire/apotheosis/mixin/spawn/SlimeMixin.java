package dev.shadowsoffire.apotheosis.mixin.spawn;

import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Slime.class)
public class SlimeMixin {

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/level/Level.addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void zenith$markMovable(RemovalReason reason, CallbackInfo ci, int size, Component name, boolean noAI, float f, int j, int k, int l, float f1, float f2, Slime slime) {
        if (noAI) {
            Slime ths = ((Slime) (Object) this);
            boolean isMoveable = ZenithComponents.MOVABLE.get(ths).getValue();
            if (ths.getCustomData().contains("zenith:movable")) {
                isMoveable = ths.getCustomData().getBoolean("zenith:movable");
                ZenithComponents.MOVABLE.get(ths).setValue(isMoveable);
                ths.getCustomData().remove("zenith:movable");
            }

            if (isMoveable) {
                ZenithComponents.MOVABLE.get(slime).setValue(true);
            }
        }
    }

}
