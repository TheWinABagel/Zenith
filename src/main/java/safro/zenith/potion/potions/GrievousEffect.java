package safro.zenith.potion.potions;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import safro.zenith.potion.PotionModule;
import safro.zenith.util.Events;

public class GrievousEffect extends MobEffect {

	public GrievousEffect() {
		super(MobEffectCategory.HARMFUL, ChatFormatting.DARK_RED.getColor());
	}
	public static void grievousEffects() {
		Events.HealEvent.EVENT.register((entity, amount) -> {
			if (entity.hasEffect(PotionModule.GRIEVOUS_EFFECT)) {
				int level = entity.getEffect(PotionModule.GRIEVOUS_EFFECT).getAmplifier() + 1;
				return (amount * Math.max(0, 1 - level * 0.4F));
			}
			return -1;
		});
	}
}