package dev.shadowsoffire.apotheosis.garden.compat;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.compat.ZenithEMIPlugin;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GardenEMIPlugin {

    public static void register(EmiRegistry registry) {
        ZenithEMIPlugin.addRecipeSafe(registry, () ->
                new EmiInfoRecipe(List.of(EmiStack.of(GardenModule.ENDER_LEAD)), List.of(Component.translatable("info.zenith.ender_lead")), Apotheosis.loc("ender_lead_info")));
    }
}
