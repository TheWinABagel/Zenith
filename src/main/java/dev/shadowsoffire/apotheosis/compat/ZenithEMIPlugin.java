package dev.shadowsoffire.apotheosis.compat;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiReloadLog;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.compat.AdventureEMIPlugin;
import dev.shadowsoffire.apotheosis.ench.compat.EnchEMIPlugin;
import dev.shadowsoffire.apotheosis.garden.compat.GardenEMIPlugin;
import dev.shadowsoffire.apotheosis.potion.compat.PotionEMIPlugin;
import dev.shadowsoffire.apotheosis.spawn.compat.SpawnerEMIPlugin;
import dev.shadowsoffire.apotheosis.village.compat.VillageEMIPlugin;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Supplier;

public class ZenithEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        if (Apotheosis.enableSpawner) {
            SpawnerEMIPlugin.register(registry);
        }
        if (Apotheosis.enableGarden) {
            GardenEMIPlugin.register(registry);
        }
        if (Apotheosis.enableAdventure) {
            AdventureEMIPlugin.register(registry);
        }
        if (Apotheosis.enablePotion) {
           PotionEMIPlugin.register(registry);
        }
        if (Apotheosis.enableVillage) {
            VillageEMIPlugin.register(registry);
        }
        if (Apotheosis.enableEnch) {
            EnchEMIPlugin.register(registry);
        }

    }

    public static EmiRenderable simplifiedRenderer(int u, int v) {
        return (raw, x, y, delta) -> {
            EmiDrawContext context = EmiDrawContext.wrap(raw);
            context.drawTexture(Apotheosis.loc("textures/gui/widgets_emi.png"), x, y, u, v, 16, 16);
        };
    }

    public static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Recipe<?> recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing zenith recipe " + EmiPort.getId(recipe));
            EmiReloadLog.error(e);
        }
    }

    public static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing EMI recipe (no ID available)");
            EmiReloadLog.error(e);
        }
    }
}
