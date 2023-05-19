package safro.zenith.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import safro.zenith.adventure.AdventureConfig;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.affix.socket.gem.bonus.GemBonus;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;
import safro.zenith.api.placebo.util.StepFunction;

import java.util.Map;
import java.util.function.Consumer;

public class EnlightenedAffix extends Affix {

	//Formatter::off
	public static final Codec<EnlightenedAffix> CODEC = RecordCodecBuilder.create(inst -> inst
		.group(
			GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
			.apply(inst, EnlightenedAffix::new)
		);
	//Formatter::on
	public static final PSerializer<EnlightenedAffix> SERIALIZER = PSerializer.fromCodec("Enlightened Affix", CODEC);

	protected final Map<LootRarity, StepFunction> values;

	public EnlightenedAffix(Map<LootRarity, StepFunction> values) {
		super(AffixType.ABILITY);
		this.values = values;
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return LootCategory.forItem(stack).isBreaker() && values.containsKey(rarity);
	}

	@Override
	public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
		list.accept(Component.translatable("affix." + this.getId() + ".desc", values.get(rarity).getInt(level)).withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public InteractionResult onItemUse(ItemStack stack, LootRarity rarity, float level, UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (AdventureConfig.torchItem.get().useOn(ctx).consumesAction()) {
			if (ctx.getItemInHand().isEmpty()) ctx.getItemInHand().grow(1);
			player.getItemInHand(ctx.getHand()).hurtAndBreak(values.get(rarity).getInt(level), player, p -> p.broadcastBreakEvent(ctx.getHand()));
			return InteractionResult.SUCCESS;
		}
		return super.onItemUse(stack, rarity, level, ctx);
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

}
