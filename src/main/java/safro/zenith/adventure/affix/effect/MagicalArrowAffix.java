package safro.zenith.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixHelper;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;

public class MagicalArrowAffix extends Affix {

	//Formatter::off
	public static final Codec<MagicalArrowAffix> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			LootRarity.CODEC.fieldOf("min_rarity").forGetter(a -> a.minRarity))
			.apply(inst, MagicalArrowAffix::new)
		);
	//Formatter::on
	public static final PSerializer<MagicalArrowAffix> SERIALIZER = PSerializer.fromCodec("Magical Arrow Affix", CODEC);

	protected LootRarity minRarity;

	public MagicalArrowAffix(LootRarity minRarity) {
		super(AffixType.ABILITY);
		this.minRarity = minRarity;
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return LootCategory.forItem(stack).isRanged() && rarity.isAtLeast(minRarity);
	}

	/*
	public void onHurt(LivingHurtEvent e) {
		if (e.getSource().getDirectEntity() instanceof AbstractArrow arrow) {
			if (AffixHelper.getAffixes(arrow).containsKey(this)) {
				e.getSource().setMagic();
			}
		}
	} */
	//@Override
	public float onHurt(DamageSource source, float amount) {
		if (source.getDirectEntity() instanceof  AbstractArrow arrow)
			if (AffixHelper.getAffixes(arrow).containsKey(this)) {
				source.setMagic();
			}
		return amount;
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

}
