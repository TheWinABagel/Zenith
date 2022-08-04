package safro.zenith.village.fletching.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import safro.zenith.Zenith;

public class BleedingEffect extends MobEffect {
    public static final DamageSource BLEEDING = new BleedingDamageSource(Zenith.MODID + ".bleeding").bypassArmor();

    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.hurt(BLEEDING, 1.0F + amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

}
