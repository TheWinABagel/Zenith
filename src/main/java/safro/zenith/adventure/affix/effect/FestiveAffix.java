package safro.zenith.adventure.affix.effect;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixHelper;
import safro.zenith.adventure.affix.AffixInstance;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.affix.socket.gem.bonus.GemBonus;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;
import safro.zenith.api.placebo.util.StepFunction;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Loot Pinata
 */
public class FestiveAffix extends Affix {

	//Formatter::off
	public static Codec<FestiveAffix> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
			.apply(inst, FestiveAffix::new)
		);
	//Formatter::on
	public static final PSerializer<FestiveAffix> SERIALIZER = PSerializer.fromCodec("Festive Affix", CODEC);
	
	protected final Map<LootRarity, StepFunction> values;

	public FestiveAffix(Map<LootRarity, StepFunction> values) {
		super(AffixType.ABILITY);
		this.values = values;
	}

	@Override
	public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
		list.accept(Component.translatable("affix." + this.getId() + ".desc", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(100 * getTrueLevel(rarity, level))).withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return LootCategory.forItem(stack).isLightWeapon() && this.values.containsKey(rarity);
	}

	private float getTrueLevel(LootRarity rarity, float level) {
		return this.values.get(rarity).get(level);
	}

	private static String MARKER = "apoth.equipment";
/*
	public void markEquipment(LivingEntityDeath e) {
		ServerLivingEntityEvents.AFTER_DEATH.register();
		e.getEntity().getAllSlots().forEach(i -> {
			if (!i.isEmpty()) i.getOrCreateTag().putBoolean(MARKER, true);
		});
	}

	public static void drops(DamageSource source, LivingEntity dead, Collection<ItemEntity> drops) { //TODO pain
		if (source.getEntity() instanceof Player player && !drops.isEmpty() && !(source.getEntity() instanceof Player)) {
			AffixInstance inst = AffixHelper.getAffixes(player.getMainHandItem()).get(this);
			if (inst != null && player.level.random.nextFloat() < getTrueLevel(inst.rarity(), inst.level())) {
				player.level.playSound(null, dead.getX(), dead.getY(), dead.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F) * 0.7F);
				((ServerLevel) player.level).sendParticles(ParticleTypes.EXPLOSION_EMITTER, dead.getX(), dead.getY(), dead.getZ(), 2, 1.0D, 0.0D, 0.0D, 0);
				for (ItemEntity item : drops) {
					if (item.getItem().hasTag() && item.getItem().getTag().contains(MARKER)) continue;
					for (int i = 0; i < 20; i++) {
						drops.add(new ItemEntity(player.level, item.getX(), item.getY(), item.getZ(), item.getItem().copy()));
					}
				}
				for (ItemEntity item : drops) {
					if (!item.getItem().getItem().canBeDepleted()) {
						item.setPos(dead.getX(), dead.getY(), dead.getZ());
						item.setDeltaMovement(-0.3 + dead.level.random.nextDouble() * 0.6, 0.3 + dead.level.random.nextDouble() * 0.3, -0.3 + dead.level.random.nextDouble() * 0.6);
					}
				}
			}
		}
		drops.stream().forEach(ent -> {
			ItemStack s = ent.getItem();
			if (s.hasTag() && s.getTag().contains(MARKER)) {
				s.getTag().remove(MARKER);
				if (s.getTag().isEmpty()) s.setTag(null);
			}
			ent.setItem(s);
		});
	}*/

	public static Affix read(JsonObject obj) {
		var values = AffixHelper.readValues(GsonHelper.getAsJsonObject(obj, "values"));
		return new FestiveAffix(values);
	}

	public JsonObject write() {
		return new JsonObject();
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeMap(this.values, (b, key) -> b.writeUtf(key.id()), (b, func) -> func.write(b));
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

	public static Affix read(FriendlyByteBuf buf) {
		Map<LootRarity, StepFunction> values = buf.readMap(b -> LootRarity.byId(b.readUtf()), b -> StepFunction.read(b));
		return new FestiveAffix(values);
	}
}
