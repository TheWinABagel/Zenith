package safro.apotheosis.potion.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.subsets.SubsetsRegistry;
import net.minecraft.resources.ResourceLocation;
import safro.apotheosis.Apotheosis;

public class PotionREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Apotheosis.MODID, "potion").toString();
    }

    @Override
    public void registerSubsets(SubsetsRegistry registry) {
        if (!Apotheosis.enablePotion) return;

    }
}
