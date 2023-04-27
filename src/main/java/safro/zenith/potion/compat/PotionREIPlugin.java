package safro.zenith.potion.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.subsets.SubsetsRegistry;
import net.minecraft.resources.ResourceLocation;
import safro.zenith.Zenith;

public class PotionREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Zenith.MODID, "potion").toString();
    }

    @Override
    public void registerSubsets(SubsetsRegistry registry) {
        if (!Zenith.enablePotion) return;
    }
}
