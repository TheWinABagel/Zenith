package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("JavadocReference")
@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {

    /**
     * Invokes the protected method {@link LivingEntity#actuallyHurt(DamageSource, float)}
     */
    @Invoker
    public void callActuallyHurt(DamageSource pDamageSrc, float pDamageAmount);

    /**
     * Invokes the private method {@link LivingEntity#checkTotemDeathProtection(DamageSource)}
     */
    @Invoker
    public boolean callCheckTotemDeathProtection(DamageSource pDamageSource);

    @Invoker
    public SoundEvent callGetDeathSound();

    @Invoker
    public float callGetSoundVolume();

    @Accessor
    void setAttackStrengthTicker(int attackStrengthTicker);

    @Invoker
    void callDropFromLootTable(DamageSource damageSource, boolean hitByPlayer);
}
