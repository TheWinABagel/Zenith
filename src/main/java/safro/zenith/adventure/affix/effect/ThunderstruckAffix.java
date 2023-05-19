package safro.zenith.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import safro.zenith.Zenith;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.affix.socket.gem.bonus.GemBonus;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;
import safro.zenith.api.placebo.util.StepFunction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Damage Chain
 */
public class ThunderstruckAffix extends Affix {

	//Formatter::off
	public static final Codec<ThunderstruckAffix> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
			.apply(inst, ThunderstruckAffix::new)
		);
	//Formatter::on
	public static final PSerializer<ThunderstruckAffix> SERIALIZER = PSerializer.fromCodec("Thunderstruck Affix", CODEC);

	protected final Map<LootRarity, StepFunction> values;

	public ThunderstruckAffix(Map<LootRarity, StepFunction> values) {
		super(AffixType.ABILITY);
		this.values = values;
	}

	@Override
	public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
		list.accept(Component.translatable("affix." + this.getId() + ".desc", (int) getTrueLevel(rarity, level)).withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return LootCategory.forItem(stack).isLightWeapon() && this.values.containsKey(rarity);
	}

	@Override
	public void doPostAttack(ItemStack stack, LootRarity rarity, float level, LivingEntity user, Entity target) {
		if (user.level.isClientSide) return;
		if (Zenith.localAtkStrength >= 0.98) {
			List<Entity> nearby = target.level.getEntities(target, new AABB(target.blockPosition()).inflate(6), CleavingAffix.cleavePredicate(user, target));
			for (Entity e : nearby) {
				e.hurt(DamageSource.LIGHTNING_BOLT, getTrueLevel(rarity, level));
			}
		}
	}

	private float getTrueLevel(LootRarity rarity, float level) {
		return this.values.get(rarity).get(level);
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

}