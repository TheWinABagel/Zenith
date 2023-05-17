package safro.zenith.adventure.affix.socket.gem.bonus;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import safro.zenith.Zenith;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.effect.DamageReductionAffix.DamageType;
import safro.zenith.adventure.affix.socket.gem.GemClass;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.util.StepFunction;

import java.util.Map;
import java.util.Objects;

public class DamageReductionBonus extends GemBonus {

	protected final DamageType type;
	protected final Map<LootRarity, StepFunction> values;

	//Formatter::off
	public static Codec<DamageReductionBonus> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			gemClass(),
			DamageType.CODEC.fieldOf("damage_type").forGetter(a -> a.type),
			VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
			.apply(inst, DamageReductionBonus::new)
		);
	//Formatter::on

	public DamageReductionBonus(GemClass gemClass, DamageType type, Map<LootRarity, StepFunction> values) {
		super(Zenith.loc("damage_reduction"), gemClass);
		this.type = type;
		this.values = values;
	}

	@Override
	public Component getSocketBonusTooltip(ItemStack gem, LootRarity rarity, int facets) {
		float level = this.values.get(rarity).get(facets);
		return Component.translatable("affix.apotheosis:damage_reduction.desc", Component.translatable("misc.apotheosis." + this.type.getId()), Affix.fmt(100 * level)).withStyle(ChatFormatting.YELLOW);
	}

	@Override
	public int getMaxFacets(LootRarity rarity) {
		return this.values.get(rarity).steps();
	}

	@Override
	public GemBonus validate() {
		Preconditions.checkNotNull(this.type, "Invalid DamageReductionBonus with null type");
		Preconditions.checkNotNull(this.values, "Invalid DamageReductionBonus with null values");
		Preconditions.checkArgument(this.values.entrySet().stream().mapMulti((entry, consumer) -> {
			consumer.accept(entry.getKey());
			consumer.accept(entry.getValue());
		}).allMatch(Objects::nonNull), "Invalid DamageReductionBonus with invalid values");
		return this;
	}

	@Override
	public boolean supports(LootRarity rarity) {
		return this.values.containsKey(rarity);
	}
	
	@Override
	public int getNumberOfUUIDs() {
		return 0;
	}

	@Override
	public Codec<? extends GemBonus> getCodec() {
		return CODEC;
	}

}
