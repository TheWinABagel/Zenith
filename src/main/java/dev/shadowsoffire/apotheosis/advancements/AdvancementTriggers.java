package dev.shadowsoffire.apotheosis.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class AdvancementTriggers {

    public static final SplittingTrigger SPLIT_BOOK = new SplittingTrigger();
    public static final ModifierTrigger SPAWNER_MODIFIER = new ModifierTrigger();
    public static final GemCutTrigger GEM_CUT = new GemCutTrigger();

    public static void init() {
        CriteriaTriggers.register(SPAWNER_MODIFIER);
        CriteriaTriggers.register(SPLIT_BOOK);
        CriteriaTriggers.register(GEM_CUT);
    }

}
