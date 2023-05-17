package safro.zenith.adventure.affix.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixType;
import safro.zenith.adventure.loot.LootRarity;
import safro.zenith.api.placebo.json.PSerializer;

import java.util.function.Consumer;

public class DurableAffix extends Affix {

	public static final PSerializer<DurableAffix> SERIALIZER = PSerializer.builtin("Durability Affix", DurableAffix::new);

	public DurableAffix() {
		super(AffixType.DURABILITY);
	}

	@Override
	public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
		return stack.isDamageableItem();
	}

	@Override
	public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
		super.addInformation(stack, rarity, level * 100, list);
	}

	@Override
	public float getDurabilityBonusPercentage(ItemStack stack, LootRarity rarity, float level, ServerPlayer user) {
		return level;
	}

	@Override
	public PSerializer<? extends Affix> getSerializer() {
		return SERIALIZER;
	}

}
