package safro.zenith.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.affix.socket.gem.bonus.GemBonus;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;
import safro.zenith.api.placebo.util.StepFunction;

import java.util.Map;
import java.util.function.Consumer;

/**
 * When blocking an explosion, gain great power.
 */
public class CatalyzingAffix extends Affix {

	//Formatter::off
	public static final Codec<CatalyzingAffix> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
			.apply(inst, CatalyzingAffix::new)
		);
	//Formatter::on
	public static final PSerializer<CatalyzingAffix> SERIALIZER = PSerializer.fromCodec("Catalyzing Affix", CODEC);

	protected final Map<LootRarity, StepFunction> values;

	public CatalyzingAffix(Map<LootRarity, StepFunction> values) {
		super(AffixType.ABILITY);
		this.values = values;
	}

	@Override
	public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
		list.accept(Component.translatable("affix." + this.getId() + ".desc").withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return LootCategory.forItem(stack) == LootCategory.SHIELD && this.values.containsKey(rarity);
	}

	@Override
	public float onShieldBlock(ItemStack stack, LootRarity rarity, float level, LivingEntity entity, DamageSource source, float amount) {
		if (source.isExplosion()) {
			int time = (int) (this.values.get(rarity).getInt(level) * amount);
			int modifier = 1 + (int) (amount / 12);
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, modifier));
		}

		return super.onShieldBlock(stack, rarity, level, entity, source, amount);
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

}