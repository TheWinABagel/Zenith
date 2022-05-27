package safro.apotheosis.advancements;

import net.fabricmc.fabric.mixin.object.builder.CriteriaAccessor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class AdvancementTriggers {

	public static final SplittingTrigger SPLIT_BOOK = new SplittingTrigger();
	public static final ModifierTrigger SPAWNER_MODIFIER = new ModifierTrigger();
	public static final EnchantedTrigger ENCHANTED = new EnchantedTrigger();

	public static void init() {
		CriteriaTriggers.CRITERIA.remove(new ResourceLocation("inventory_changed"));
		CriteriaTriggers.INVENTORY_CHANGED = CriteriaTriggers.register(new ExtendedInvTrigger());
		CriteriaTriggers.CRITERIA.replace(CriteriaTriggers.ENCHANTED_ITEM.getId(), ENCHANTED);
		CriteriaAccessor.callRegister(AdvancementTriggers.SPAWNER_MODIFIER);
		CriteriaAccessor.callRegister(AdvancementTriggers.SPLIT_BOOK);
	}

}