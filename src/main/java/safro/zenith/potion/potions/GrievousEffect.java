package safro.zenith.potion.potions;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import safro.zenith.adventure.affix.effect.PotionAffix;
import safro.zenith.potion.PotionModule;

public class GrievousEffect extends MobEffect {

	public GrievousEffect() {
		super(MobEffectCategory.HARMFUL, ChatFormatting.DARK_RED.getColor());
	}
	public static float GrievousEffects(Float f, LivingEntity entity) {
		if (entity.hasEffect(PotionModule.GRIEVOUS_EFFECT)) {
			int level = entity.getEffect(PotionModule.GRIEVOUS_EFFECT).getAmplifier() + 1;
			return (f * Math.max(0, 1 - level * 0.4F));
		}
		return -1;
	}
}