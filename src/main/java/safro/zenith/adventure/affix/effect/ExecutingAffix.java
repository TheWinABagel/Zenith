package safro.zenith.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import safro.zenith.Zenith;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.affix.socket.gem.bonus.GemBonus;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;
import safro.zenith.api.placebo.util.StepFunction;
import safro.zenith.mixin.LivingEntityInvoker;

import java.util.Map;
import java.util.function.Consumer;

public class ExecutingAffix extends Affix {

	//Formatter::off
	public static final Codec<ExecutingAffix> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
			.apply(inst, ExecutingAffix::new)
		);
	//Formatter::on
	public static final PSerializer<ExecutingAffix> SERIALIZER = PSerializer.fromCodec("Executing Affix", CODEC);

	protected final Map<LootRarity, StepFunction> values;

	public ExecutingAffix(Map<LootRarity, StepFunction> values) {
		super(AffixType.ABILITY);
		this.values = values;
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return LootCategory.forItem(stack) == LootCategory.HEAVY_WEAPON && values.containsKey(rarity);
	}

	@Override
	public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
		list.accept(Component.translatable("affix." + this.getId() + ".desc", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(100 * getTrueLevel(rarity, level))).withStyle(ChatFormatting.YELLOW));
	}

	private float getTrueLevel(LootRarity rarity, float level) {
		return this.values.get(rarity).get(level);
	}

	@Override
	public void doPostAttack(ItemStack stack, LootRarity rarity, float level, LivingEntity user, Entity target) {
		float threshold = getTrueLevel(rarity, level);
		if (Zenith.localAtkStrength >= 0.98 && target instanceof LivingEntity living && !living.level.isClientSide) {
			if (living.getHealth() / living.getMaxHealth() < threshold) {
				DamageSource src = new EntityDamageSource("apotheosis.execute", user).bypassArmor().bypassMagic();
				if (!((LivingEntityInvoker) living).callCheckTotemDeathProtection(src)) {
					SoundEvent soundevent = ((LivingEntityInvoker) living).callGetDeathSound();
					if (soundevent != null) {
						living.playSound(soundevent, ((LivingEntityInvoker) living).callGetSoundVolume(), living.getVoicePitch());
					}

					living.setHealth(0);
					living.die(src);
				}
			}
		}
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

}
