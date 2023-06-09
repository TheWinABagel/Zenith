package safro.zenith.potion.potions;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import safro.zenith.potion.PotionModule;

public class VitalityEffect extends MobEffect {

	public VitalityEffect() {
		super(MobEffectCategory.BENEFICIAL, ChatFormatting.RED.getColor());
	}

	public static float vitalityEffects(Float f, LivingEntity entity) {
		if (entity.hasEffect(PotionModule.VITALITY_EFFECT)) {
			int level = entity.getEffect(PotionModule.VITALITY_EFFECT).getAmplifier() + 1;
			return (f * (level * 0.2F));
		}
		return 0;
	}

}