package dev.shadowsoffire.apotheosis.mixin.adventure;

import dev.shadowsoffire.apotheosis.util.Events;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * Used to make the glowing effect on mobs use their name color.
     */
    @Override
    public int getTeamColor() {
        int color = super.getTeamColor();
        if (color == 16777215) {
            Component name = this.getCustomName();
            if (name != null && name.getStyle().getColor() != null) color = name.getStyle().getColor().getValue();
        }
        return color;
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci){
        Events.OnEntityDeath.LIVING_DEATH.invoker().onDeath(((LivingEntity) (Object) this), damageSource);
    }
}
