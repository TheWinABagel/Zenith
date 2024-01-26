package safro.zenith.potion.potions;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import safro.zenith.potion.PotionModule;
import safro.zenith.util.Events;

public class VitalityEffect extends MobEffect {

	public VitalityEffect() {
		super(MobEffectCategory.BENEFICIAL, ChatFormatting.RED.getColor());
	}

	public static void vitalityEffects() {
		Events.HealEvent.EVENT.register((entity, amount) -> {
			if (entity.hasEffect(PotionModule.VITALITY_EFFECT)) {
				int level = entity.getEffect(PotionModule.VITALITY_EFFECT).getAmplifier() + 1;
				return (amount * (level * 0.2F));
			}
			return 0;
		});
	}

}